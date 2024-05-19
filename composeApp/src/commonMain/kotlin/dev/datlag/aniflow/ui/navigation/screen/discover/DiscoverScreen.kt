package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.scrollUpVisible
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(component: DiscoverComponent) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }

            val activePadding by animateDpAsState(
                targetValue = if (active) 0.dp else 16.dp
            )

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = activePadding)
                    .padding(bottom = activePadding),
                query = query,
                onQueryChange = {
                    query = it
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                onSearch = {
                    query = it
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null
                    )
                },
                placeholder = {
                    Text(text = stringResource(SharedRes.strings.search))
                },
                content = { }
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
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
            contentPadding = padding.merge(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text("Discover Screen")
            }
        }
    }
}