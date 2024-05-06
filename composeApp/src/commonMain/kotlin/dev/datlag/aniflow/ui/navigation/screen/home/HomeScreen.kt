package dev.datlag.aniflow.ui.navigation.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.LocalPadding
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.ui.navigation.screen.component.CollapsingToolbar
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.home.component.AllLoadingView
import dev.datlag.aniflow.ui.navigation.screen.home.component.DefaultOverview
import dev.datlag.aniflow.ui.navigation.screen.home.component.ScheduleOverview
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingToolbar(
                state = appBarState,
                scrollBehavior = scrollBehavior,
                viewTypeFlow = component.viewing,
                onProfileClick = component::viewProfile,
                onAnimeClick = component::viewAnime,
                onMangaClick = component::viewManga
            )
        },
        floatingActionButton = {

        },
        bottomBar = {
            HidingNavigationBar(
                visible = listState.isScrollingUp()
            )
        }
    ) {
        CompositionLocalProvider(
            LocalPaddingValues provides it
        ) {
            AllLoadingView(
                loadingContent = {

                }
            ) {
                val viewType by component.viewing.collectAsStateWithLifecycle(MediaType.UNKNOWN__)
                val isManga = remember(viewType) {
                    viewType == MediaType.MANGA
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = LocalPadding(top = 16.dp)
                ) {
                    if (!isManga) {
                        item {
                            ScheduleOverview(
                                flow = component.airing,
                                onMoreClick = { },
                                onMediumClick = component::details
                            )
                        }
                    }
                    item {
                        DefaultOverview(
                            title = "Trending",
                            flow = component.trending,
                            onMoreClick = { },
                            onMediumClick = component::details
                        )
                    }
                    item {
                        DefaultOverview(
                            title = "Popular",
                            flow = component.popularNow,
                            onMoreClick = { },
                            onMediumClick = component::details
                        )
                    }
                    if (!isManga) {
                        item {
                            DefaultOverview(
                                title = "Popular Next",
                                flow = component.popularNext,
                                onMoreClick = { },
                                onMediumClick = component::details
                            )
                        }
                    }
                }
            }
        }
    }
}