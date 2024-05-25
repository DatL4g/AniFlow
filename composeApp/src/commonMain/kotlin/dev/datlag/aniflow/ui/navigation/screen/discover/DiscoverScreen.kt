package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.common.animatePaddingAsState
import dev.datlag.aniflow.common.header
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.common.scrollUpVisible
import dev.datlag.aniflow.other.rememberSearchBarState
import dev.datlag.aniflow.ui.custom.ErrorContent
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.discover.component.HidingSearchBar
import dev.datlag.aniflow.ui.navigation.screen.discover.component.RecommendationCard
import dev.datlag.aniflow.ui.navigation.screen.discover.component.TypeCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(component: DiscoverComponent) {
    val listState = rememberLazyGridState()
    val searchBarState = rememberSearchBarState()

    Scaffold(
        topBar = {
            HidingSearchBar(
                visible = true,
                searchBarState = searchBarState,
                component = component
            )
        },
        bottomBar = {
            HidingNavigationBar(
                visible = listState.scrollUpVisible() && !searchBarState.isActive,
                selected = NavigationBarState.Discover,
                loggedIn = component.loggedIn,
                onDiscover = { },
                onHome = component::viewHome,
                onFavorites = component::viewList
            )
        }
    ) { targetPadding ->
        val smoothPadding by animatePaddingAsState(
            targetValues = targetPadding.merge(PaddingValues(16.dp))
        )
        val discoverState by component.state.collectAsStateWithLifecycle()

        when (val current = discoverState) {
            is DiscoverState.Recommended.None, is DiscoverState.Recommended.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
                    )
                }
            }
            is DiscoverState.Error -> {
                ErrorContent(
                    modifier = Modifier.fillMaxSize().padding(smoothPadding)
                )
            }
            is DiscoverState.Success -> {
                LazyVerticalGrid(
                    state = listState,
                    modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                    contentPadding = smoothPadding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    items(current.collection.toList(), key = { it.id }) {
                        Text(text = it.preferred(null))
                    }
                }
            }
            else -> { }
        }
    }
}