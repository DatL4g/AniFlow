package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.MediumStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.notPreferred
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.custom.shareHandler
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    initialMedium: Medium,
    titleLanguageFlow: Flow<AppSettings.TitleLanguage?>,
    mediumStateFlow: StateFlow<MediumStateMachine.State>,
    bannerImageFlow: Flow<String?>,
    coverImage: Medium.CoverImage,
    titleFlow: Flow<Medium.Title>,
    isFavoriteFlow: Flow<Boolean>,
    isFavoriteBlockedFlow: Flow<Boolean>,
    siteUrlFlow: Flow<String>,
    showShare: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val bannerImage by bannerImageFlow.collectAsStateWithLifecycle(initialMedium.bannerImage)
        val isCollapsed by remember(state) {
            derivedStateOf { state.collapsedFraction >= 0.99F }
        }
        val imageAlpha by remember(state) {
            derivedStateOf {
                max(min(1F - state.collapsedFraction, 1F), 0F)
            }
        }

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize(),
            model = bannerImage,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            error = rememberAsyncImagePainter(
                model = coverImage.extraLarge,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = coverImage.large,
                    contentScale = ContentScale.Crop
                )
            ),
            alpha = imageAlpha
        )
        LargeTopAppBar(
            navigationIcon = {
                IconButton(
                    modifier = if (isCollapsed) {
                        Modifier
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75F), CircleShape)
                    },
                    onClick = {
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null
                    )
                }
            },
            title = {


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    val title by titleFlow.collectAsStateWithLifecycle(initialMedium.title)
                    val titleLanguage by titleLanguageFlow.collectAsStateWithLifecycle(null)

                    Text(
                        text = title.preferred(titleLanguage),
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = if (!isCollapsed) {
                            LocalTextStyle.current.copy(
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.surface,
                                    offset = Offset(4F, 4F),
                                    blurRadius = 8F
                                )
                            )
                        } else {
                            LocalTextStyle.current
                        }
                    )
                    title.notPreferred(titleLanguage)?.let {
                        Text(
                            text = it,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = if (!isCollapsed) {
                                MaterialTheme.typography.labelMedium.copy(
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.surface,
                                        offset = Offset(4F, 4F),
                                        blurRadius = 8F
                                    )
                                )
                            } else {
                                MaterialTheme.typography.labelMedium
                            }
                        )
                    }
                }
            },
            actions = {
                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .ifFalse(isCollapsed) {
                            background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75F), CircleShape)
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val mediumState by mediumStateFlow.collectAsStateWithLifecycle()
                    val siteUrl by siteUrlFlow.collectAsStateWithLifecycle(initialMedium.siteUrl)
                    val shareHandler = shareHandler()

                    AnimatedVisibility(
                        visible = mediumState.isSuccess,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val isFavoriteBlocked by isFavoriteBlockedFlow.collectAsStateWithLifecycle(initialMedium.isFavoriteBlocked)
                        val isFavorite by isFavoriteFlow.collectAsStateWithLifecycle(initialMedium.isFavorite)
                        var favoriteChanged by remember(isFavorite) { mutableStateOf<Boolean?>(null) }

                        IconButton(
                            onClick = {
                                favoriteChanged = !(favoriteChanged ?: isFavorite)
                                onToggleFavorite()
                            },
                            enabled = !isFavoriteBlocked
                        ) {
                            Icon(
                                imageVector = if (favoriteChanged ?: isFavorite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = null
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = showShare,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = {
                                shareHandler.share(siteUrl)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            modifier = Modifier.hazeChild(
                state = LocalHaze.current,
                style = HazeMaterials.thin(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ).fillMaxWidth()
        )
    }
}