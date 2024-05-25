package dev.datlag.aniflow.ui.navigation.screen.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.ui.navigation.screen.home.component.default.GenreChip
import dev.datlag.aniflow.ui.navigation.screen.home.component.default.Rating
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.aniflow.ui.theme.rememberSchemeThemeDominantColorState
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MediumCard(
    medium: Medium,
    titleLanguage: SettingsTitle?,
    modifier: Modifier = Modifier,
    onClick: (Medium) -> Unit
) {
    val defaultColor = remember(medium.coverImage.color) {
        medium.coverImage.color?.substringAfter('#')?.let {
            val colorValue = it.hexToLong() or 0x00000000FF000000
            Color(colorValue)
        }
    }

    SchemeTheme(
        key = medium.id,
        defaultColor = defaultColor,
    ) { updater ->
        Card(
            modifier = modifier,
            onClick = {
                onClick(medium)
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val colorState = rememberSchemeThemeDominantColorState(
                    key = medium.id,
                    applyMinContrast = true,
                    minContrastBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    defaultColor = defaultColor ?: MaterialTheme.colorScheme.primary,
                    defaultOnColor = defaultColor?.plainOnColor ?: MaterialTheme.colorScheme.onPrimary
                )

                AsyncImage(
                    model = medium.coverImage.extraLarge,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = medium.title.userPreferred,
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

                medium.genres.firstOrNull()?.let {
                    GenreChip(
                        label = it,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .bottomShadowBrush(colorState.primary)
                        .padding(16.dp)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = medium.preferred(titleLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = if (medium.averageScore > 0F) {
                            1
                        } else {
                            2
                        },
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                        color = colorState.onPrimary
                    )
                    if (medium.averageScore > 0F) {
                        Rating(medium, colorState.onPrimary)
                    }
                }
            }
        }
    }
}