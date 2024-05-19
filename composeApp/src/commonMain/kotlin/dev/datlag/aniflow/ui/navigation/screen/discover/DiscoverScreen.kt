package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.common.scrollUpVisible
import dev.datlag.aniflow.ui.custom.ErrorContent
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.discover.component.SearchResult
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(component: DiscoverComponent) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            val type by component.type.collectAsStateWithLifecycle(MediaType.UNKNOWN__)
            var query by remember { mutableStateOf(component.initialSearchValue ?: "") }
            var active by remember { mutableStateOf(false) }

            val activePadding by animateDpAsState(
                targetValue = if (active) 0.dp else 16.dp
            )

            LaunchedEffect(query) {
                component.search(query)
            }

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
                    Text(
                        text = stringResource(
                            if (type == MediaType.MANGA) {
                                SharedRes.strings.search_manga
                            } else {
                                SharedRes.strings.search_anime
                            }
                        )
                    )
                },
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnimatedVisibility(
                            visible = query.isNotBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = {
                                    query = ""
                                    active = false
                                },
                                enabled = query.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                active = true
                                component.toggleView()
                            },
                            enabled = type != MediaType.UNKNOWN__
                        ) {
                            if (type == MediaType.ANIME) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.PlayCircle,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                content = {
                    val resultState by component.searchResult.collectAsStateWithLifecycle(CollectionState.None)

                    when (val current = resultState) {
                        is CollectionState.None -> {}
                        is CollectionState.Error -> {
                            ErrorContent(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        is CollectionState.Success -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(current.collection.toList(), key = { it.id }) {
                                    SearchResult(
                                        medium = it,
                                        modifier = Modifier.fillParentMaxWidth(),
                                        onClick = component::details
                                    )
                                }
                            }
                        }
                    }
                }
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