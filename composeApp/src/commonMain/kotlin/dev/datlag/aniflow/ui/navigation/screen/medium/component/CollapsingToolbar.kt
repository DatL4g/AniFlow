package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.notPreferred
import dev.datlag.aniflow.common.preferred
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    bannerImageFlow: StateFlow<String?>,
    coverImage: Medium.CoverImage,
    titleFlow: StateFlow<Medium.Title>,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val bannerImage by bannerImageFlow.collectAsStateWithLifecycle()
        val isCollapsed by remember(state) {
            derivedStateOf { state.collapsedFraction >= 0.99F }
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
            alpha = 1F - state.collapsedFraction
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
                val title by titleFlow.collectAsStateWithLifecycle()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = title.preferred(),
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
                    title.notPreferred()?.let {
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