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
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
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
import dev.datlag.aniflow.anilist.MediumRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.notPreferred
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.other.rememberInstantAppHelper
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.custom.shareHandler
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumComponent
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max
import kotlin.math.min
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    coverImage: Medium.CoverImage,
    showShare: Boolean,
    component: MediumComponent
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val bannerImage by component.bannerImage.collectAsStateWithLifecycle(component.initialMedium.bannerImage)
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
                        component.back()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null
                    )
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    val title by component.title.collectAsStateWithLifecycle(component.initialMedium.title)
                    val titleLanguage by component.titleLanguage.collectAsStateWithLifecycle(null)

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
                    val mediumState by component.mediumState.collectAsStateWithLifecycle(null)
                    val siteUrl by component.siteUrl.collectAsStateWithLifecycle(component.initialMedium.siteUrl)
                    val shareHandler = shareHandler()
                    val instantAppHelper = rememberInstantAppHelper()

                    AnimatedVisibility(
                        visible = mediumState is MediumRepository.State.Success && !instantAppHelper.isInstantApp,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val loggedIn by component.isLoggedIn.collectAsStateWithLifecycle(false)
                        val isFavoriteBlocked by component.isFavoriteBlocked.collectAsStateWithLifecycle(component.initialMedium.isFavoriteBlocked)
                        val isFavorite by component.isFavorite.collectAsStateWithLifecycle(component.initialMedium.isFavorite)
                        var favoriteChanged by remember(isFavorite) { mutableStateOf<Boolean?>(null) }
                        val uriHandler = LocalUriHandler.current

                        IconButton(
                            onClick = {
                                if (!loggedIn) {
                                    uriHandler.openUri(component.loginUri)
                                } else {
                                    favoriteChanged = !(favoriteChanged ?: isFavorite)
                                    component.toggleFavorite()
                                }
                            },
                            enabled = !loggedIn || !isFavoriteBlocked
                        ) {
                            Icon(
                                imageVector = if (favoriteChanged ?: isFavorite) {
                                    Icons.Rounded.Favorite
                                } else {
                                    Icons.Rounded.FavoriteBorder
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