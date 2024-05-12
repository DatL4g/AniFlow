package dev.datlag.aniflow.ui.navigation.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.other.rememberInstantAppHelper
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    userFlow: Flow<User?>,
    viewTypeFlow: Flow<MediaType>,
    onProfileClick: () -> Unit,
    onAnimeClick: () -> Unit,
    onMangaClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val isCollapsed by remember(state) {
            derivedStateOf { state.collapsedFraction >= 0.99F }
        }
        val imageAlpha by remember(state) {
            derivedStateOf {
                max(min(1F - state.collapsedFraction, 1F), 0F)
            }
        }
        val viewType by viewTypeFlow.collectAsStateWithLifecycle(MediaType.UNKNOWN__)
        val isManga = remember(viewType) {
            viewType == MediaType.MANGA
        }

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize(),
            painter = painterResource(SharedRes.images.banner),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            alpha = imageAlpha
        )

        LargeTopAppBar(
            navigationIcon = {
                val helper = rememberInstantAppHelper()

                IconButton(
                    modifier = Modifier.ifFalse(isCollapsed) {
                        background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75F), CircleShape)
                    },
                    onClick = {
                        if (helper.isInstantApp) {
                            helper.showInstallPrompt()
                        } else {
                            onProfileClick()
                        }
                    }
                ) {
                    val user by userFlow.collectAsStateWithLifecycle(null)
                    val tintColor = LocalContentColor.current
                    var colorFilter by remember(user, tintColor) {
                        mutableStateOf<ColorFilter?>(
                            if (user == null) ColorFilter.tint(tintColor) else null
                        )
                    }

                    AsyncImage(
                        model = user?.avatar?.large,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = colorFilter,
                        placeholder = rememberVectorPainter(Icons.Filled.AccountCircle),
                        error = rememberAsyncImagePainter(
                            model = user?.avatar?.medium,
                            contentScale = ContentScale.Crop,
                            error = rememberVectorPainter(Icons.Filled.AccountCircle),
                            placeholder = rememberVectorPainter(Icons.Filled.AccountCircle),
                            onError = {
                                colorFilter = ColorFilter.tint(tintColor)
                            },
                            onSuccess = {
                                colorFilter = null
                            },
                            onLoading = {
                                colorFilter = ColorFilter.tint(tintColor)
                            }
                        ),
                        onSuccess = {
                            colorFilter = null
                        },
                        onLoading = {
                            colorFilter = ColorFilter.tint(tintColor)
                        }
                    )
                }
            },
            title = {
                AnimatedVisibility(
                    visible = isCollapsed
                ) {
                    Text(
                        text = stringResource(SharedRes.strings.app_name),
                        fontFamily = fontFamilyResource(SharedRes.fonts.marckscript_regular)
                    )
                }
            },
            actions = {
                Row(
                    modifier = Modifier.ifFalse(isCollapsed) {
                        background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75F), CircleShape)
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(
                        onClick = {
                            onAnimeClick()
                        },
                        enabled = isManga
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircleFilled,
                            contentDescription = null,
                            tint = if (isManga) {
                                LocalContentColor.current
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            onMangaClick()
                        },
                        enabled = !isManga
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                            contentDescription = null,
                            tint = if (isManga) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current
                            }
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            modifier = Modifier.hazeChild(
                state = LocalHaze.current,
                style = HazeMaterials.thin(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            ).fillMaxWidth()
        )
    }
}