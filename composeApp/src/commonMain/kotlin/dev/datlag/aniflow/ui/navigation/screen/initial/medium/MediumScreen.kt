package dev.datlag.aniflow.ui.navigation.screen.initial.medium

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.common.LocalPadding
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.preferredTitle
import dev.datlag.aniflow.common.shimmer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun MediumScreen(component: MediumComponent) {
    val scrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollState.nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth().matchParentSize(),
                    model = component.initialMedium.bannerImage,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                MediumTopAppBar(
                    title = {
                        Text(
                            text = component.initialMedium.preferredTitle(),
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    scrollBehavior = scrollState,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) {
        CompositionLocalProvider(
            LocalPaddingValues provides LocalPadding().merge(it)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().haze(LocalHaze.current),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = LocalPadding()
            ) {
                repeat(10) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxWidth().height(150.dp).shimmer()
                        )
                    }
                }
            }
        }
    }
}