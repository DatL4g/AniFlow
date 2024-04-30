package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.bottomShadowBrush
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.custom.alignment.rememberParallaxAlignment
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.aniflow.ui.theme.rememberSchemeThemeDominantColorState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MediumCard(
    medium: Medium,
    titleLanguageFlow: Flow<AppSettings.TitleLanguage?>,
    modifier: Modifier = Modifier,
    onClick: (Medium) -> Unit
) {
    SchemeTheme(
        key = medium.id
    ) {
        Card(
            modifier = modifier,
            onClick = {
                onClick(medium)
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val color = medium.coverImage.color?.substringAfter('#')?.let {
                    val colorValue = it.hexToLong() or 0x00000000FF000000
                    Color(colorValue)
                }
                val colorState = rememberSchemeThemeDominantColorState(
                    key = medium.id,
                    applyMinContrast = true,
                    minContrastBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    defaultColor = color ?: MaterialTheme.colorScheme.primary,
                    defaultOnColor = contentColorFor(color ?: MaterialTheme.colorScheme.primary)
                )
                var successPainter by remember { mutableStateOf<Painter?>(null) }

                SchemeTheme.update(medium.id, successPainter)

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
                                successPainter = state.painter
                            },
                            onError = {
                                successPainter = color?.let(::ColorPainter)
                            }
                        ),
                        onSuccess = { state ->
                            successPainter = state.painter
                        }
                    ),
                    onSuccess = { state ->
                        successPainter = state.painter
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
                        .bottomShadowBrush(colorState.color)
                        .padding(16.dp)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val titleLanguage by titleLanguageFlow.collectAsStateWithLifecycle(null)

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
                        color = colorState.onColor
                    )
                    if (medium.averageScore > 0F) {
                        Rating(medium, colorState.onColor)
                    }
                }
            }
        }
    }
}