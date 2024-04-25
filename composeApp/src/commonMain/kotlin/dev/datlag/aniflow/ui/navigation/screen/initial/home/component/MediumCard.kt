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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.bottomShadowBrush
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.common.shimmerPainter
import dev.datlag.aniflow.ui.custom.alignment.rememberParallaxAlignment
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.aniflow.ui.theme.rememberSchemeThemeDominantColorState

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MediumCard(
    medium: Medium,
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
                val scope = rememberCoroutineScope()
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
                val animatedColor by animateColorAsState(
                    targetValue = colorState.color,
                    animationSpec = tween()
                )

                AsyncImage(
                    model = medium.coverImage.extraLarge,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = medium.title.userPreferred,
                    contentScale = ContentScale.Crop,
                    placeholder = shimmerPainter(),
                    error = rememberAsyncImagePainter(
                        model = medium.coverImage.large,
                        contentScale = ContentScale.Crop,
                        placeholder = shimmerPainter(),
                        error = rememberAsyncImagePainter(
                            model = medium.coverImage.medium,
                            contentScale = ContentScale.Crop,
                            placeholder = shimmerPainter(),
                            onSuccess = { state ->
                                SchemeTheme.update(
                                    key = medium.id,
                                    input = state.painter,
                                    scope = scope
                                )
                            },
                            onError = {
                                SchemeTheme.update(
                                    key = medium.id,
                                    color = color,
                                    scope = scope
                                )
                            }
                        ),
                        onSuccess = { state ->
                            SchemeTheme.update(
                                key = medium.id,
                                input = state.painter,
                                scope = scope
                            )
                        }
                    ),
                    onSuccess = { state ->
                        SchemeTheme.update(
                            key = medium.id,
                            input = state.painter,
                            scope = scope
                        )
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
                        .bottomShadowBrush(animatedColor)
                        .padding(16.dp)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = medium.preferred(),
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