package dev.datlag.aniflow.ui.navigation.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.flow.Flow
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
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
                IconButton(
                    modifier = Modifier.ifFalse(isCollapsed) {
                        background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75F), CircleShape)
                    },
                    onClick = {
                        onProfileClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null
                    )
                }
            },
            title = {
                AnimatedVisibility(
                    visible = isCollapsed
                ) {
                    Text(text = "AniFlow")
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
                            imageVector = Icons.AutoMirrored.Default.MenuBook,
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