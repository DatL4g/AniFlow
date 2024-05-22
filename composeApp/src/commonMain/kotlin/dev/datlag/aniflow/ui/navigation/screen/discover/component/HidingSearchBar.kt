package dev.datlag.aniflow.ui.navigation.screen.discover.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.state.SearchState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.other.SearchBarState
import dev.datlag.aniflow.other.rememberSearchBarState
import dev.datlag.aniflow.ui.custom.ErrorContent
import dev.datlag.aniflow.ui.navigation.screen.discover.DiscoverComponent
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HidingSearchBar(
    visible: Boolean,
    searchBarState: SearchBarState = rememberSearchBarState(),
    component: DiscoverComponent
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = {
                with(density) { -it.dp.roundToPx() }
            },
            animationSpec = tween(
                easing = LinearOutSlowInEasing,
                durationMillis = 500
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(
                easing = LinearOutSlowInEasing,
                durationMillis = 500
            )
        )
    ) {
        val type by component.type.collectAsStateWithLifecycle(MediaType.UNKNOWN__)
        var query by remember { mutableStateOf(component.initialSearchValue ?: "") }

        val activePadding by animateDpAsState(
            targetValue = if (searchBarState.isActive) 0.dp else 16.dp
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = activePadding)
                .padding(bottom = activePadding),
            query = query,
            onQueryChange = {
                query = it
                component.search(query)
            },
            active = searchBarState.isActive,
            onActiveChange = {
                searchBarState.isActive = it
            },
            onSearch = {
                query = it
                component.search(query)
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
                                searchBarState.isActive = false
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
                            searchBarState.isActive = true
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
                val resultState by component.searchResult.collectAsStateWithLifecycle()

                when (val current = resultState) {
                    is SearchState.None -> { }
                    is SearchState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
                            )
                        }
                    }
                    is SearchState.Error -> {
                        ErrorContent(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is SearchState.Success -> {
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
    }
}