package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.common.*
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun MediumScreen(component: MediumComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val coverImage by component.coverImage.collectAsStateWithLifecycle()
    val isCollapsed by remember(appBarState) {
        derivedStateOf { appBarState.collapsedFraction >= 0.99F }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollState.nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                val bannerImage by component.bannerImage.collectAsStateWithLifecycle()

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
                    alpha = 1F - appBarState.collapsedFraction
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
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        val title by component.title.collectAsStateWithLifecycle()

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
                    scrollBehavior = scrollState,
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
    ) {
        CompositionLocalProvider(
            LocalPaddingValues provides LocalPadding().merge(it)
        ) {
            val description by component.description.collectAsStateWithLifecycle()

            LazyColumn(
                modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = LocalPadding(top = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .width(140.dp)
                                .height(200.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = MaterialTheme.shapes.medium,
                                    spotColor = MaterialTheme.colorScheme.primary
                                ),
                            model = coverImage.extraLarge,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            placeholder = shimmerPainter(),
                            error = rememberAsyncImagePainter(
                                model = coverImage.large,
                                contentScale = ContentScale.Crop,
                                placeholder = shimmerPainter(),
                                error = rememberAsyncImagePainter(
                                    model = coverImage.medium,
                                    contentScale = ContentScale.Crop,
                                    placeholder = shimmerPainter()
                                )
                            )
                        )
                        Column(
                            modifier = Modifier.weight(1F).fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Episode Info etc.")
                        }
                    }
                }
                if (!description.isNullOrBlank()) {
                    item {
                        Text(
                            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
                            text = "Description",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        SelectionContainer {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = description!!.htmlToAnnotatedString()
                            )
                        }
                    }
                }
            }
        }
    }
}