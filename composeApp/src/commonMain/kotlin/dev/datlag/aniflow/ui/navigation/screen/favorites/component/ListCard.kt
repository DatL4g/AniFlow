package dev.datlag.aniflow.ui.navigation.screen.favorites.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.aniflow.ui.theme.rememberSchemeThemeDominantColorState
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ListCard(
    medium: Medium,
    titleLanguage: TitleLanguage?,
    modifier: Modifier = Modifier,
    onClick: (Medium) -> Unit,
    onIncrease: (Medium, Int) -> Unit
) {
    val defaultColor = remember(medium.coverImage.color) {
        medium.coverImage.color?.substringAfter('#')?.let {
            val colorValue = it.hexToLong() or 0x00000000FF000000
            Color(colorValue)
        }
    }

    val updater = SchemeTheme.create(
        key = medium.id,
        defaultColor = defaultColor,
    )

    ElevatedCard(
        onClick = { onClick(medium) },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .widthIn(min = 100.dp, max = 120.dp)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.medium)
            ) {
                val colorState = rememberSchemeThemeDominantColorState(
                    key = medium.id,
                    applyMinContrast = true,
                    minContrastBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    defaultColor = defaultColor ?: MaterialTheme.colorScheme.primary,
                    defaultOnColor = defaultColor?.plainOnColor ?: MaterialTheme.colorScheme.onPrimary
                )

                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = medium.coverImage.extraLarge,
                    contentDescription = medium.preferred(titleLanguage),
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = medium.coverImage.large,
                        contentScale = ContentScale.Crop,
                        error = rememberAsyncImagePainter(
                            model = medium.coverImage.medium,
                            contentScale = ContentScale.Crop,
                            onSuccess = { state ->
                                updater?.update(state.painter)
                            }
                        ),
                        onSuccess = { state ->
                            updater?.update(state.painter)
                        }
                    ),
                    onSuccess = { state ->
                        updater?.update(state.painter)
                    }
                )
                medium.entry?.let { entry ->
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .bottomShadowBrush(colorState.primary)
                            .padding(8.dp)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = entry.status.icon(),
                            contentDescription = null,
                            tint = colorState.onPrimary
                        )
                        Text(
                            text = stringResource(entry.status.stringRes(medium.type)),
                            color = colorState.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = true
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1F).padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                var progress by remember(medium.id) { mutableStateOf(medium.entry?.progress ?: 0) }
                val totalEpisodes = remember(medium.episodesOrChapters) { max(medium.episodesOrChapters, 0) }
                val maxProgress = remember(progress, totalEpisodes) { max(max(progress, totalEpisodes), 0) }

                Text(
                    text = medium.preferred(titleLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                    modifier = Modifier.fillMaxWidth().weight(1F),
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(text = "$progress/$totalEpisodes")
                    AnimatedVisibility(
                        visible = progress < totalEpisodes
                    ) {
                        Button(
                            onClick = {
                                progress++
                                onIncrease(medium, progress)
                            },
                            enabled = progress < totalEpisodes
                        ) {
                            Text(text = stringResource(SharedRes.strings.plus_one))
                        }
                    }
                }
                if (maxProgress > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LinearProgressIndicator(
                            progress = { 1F },
                            modifier = Modifier
                                .fillMaxWidth(
                                    fraction = (progress.toFloat() / maxProgress.toFloat())
                                )
                                .clip(CircleShape)
                        )
                        if (progress < maxProgress) {
                            LinearProgressIndicator(
                                progress = { 0F },
                                modifier = Modifier.weight(1F).clip(CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}