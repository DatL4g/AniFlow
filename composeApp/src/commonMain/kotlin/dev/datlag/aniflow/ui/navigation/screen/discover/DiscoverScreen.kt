package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.common.animatePaddingAsState
import dev.datlag.aniflow.common.header
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.scrollUpVisible
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.discover.component.HidingSearchBar
import dev.datlag.aniflow.ui.navigation.screen.discover.component.RecommendationCard
import dev.datlag.aniflow.ui.navigation.screen.discover.component.TypeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(component: DiscoverComponent) {
    val listState = rememberLazyGridState()

    Scaffold(
        topBar = {
            HidingSearchBar(
                visible = listState.scrollUpVisible(),
                component = component
            )
        },
        bottomBar = {
            HidingNavigationBar(
                visible = listState.scrollUpVisible(),
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

        LazyVerticalGrid(
            state = listState,
            modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
            contentPadding = smoothPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(2)
        ) {
            header {
                RecommendationCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
            item {
                TypeCard(
                    type = DiscoverType.Top100,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
            item {
                TypeCard(
                    type = DiscoverType.TopMovies,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
            item {
                TypeCard(
                    type = DiscoverType.Spring,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
            item {
                TypeCard(
                    type = DiscoverType.Summer,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
            item {
                TypeCard(
                    type = DiscoverType.Fall,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
            item {
                TypeCard(
                    type = DiscoverType.Winter,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                )
            }
        }
    }
}