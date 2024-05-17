package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.ListRepository
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.favorites.component.ListCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun FavoritesScreen(component: FavoritesComponent) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(SharedRes.strings.app_name))
                },
                actions = {

                },
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                expanded = listState.isScrollingUp(),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.SwapVert,
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = "Sort")
                }
            )
        },
        bottomBar = {
            HidingNavigationBar(
                visible = listState.isScrollingUp(),
                selected = NavigationBarState.Favorite,
                loggedIn = flowOf(true),
                onDiscover = component::viewDiscover,
                onHome = component::viewHome,
                onFavorites = { }
            )
        }
    ) { padding ->
        val state by component.listState.collectAsStateWithLifecycle()

        when (val current = state) {
            is ListRepository.State.None -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.2F).clip(CircleShape)
                    )
                }
            }
            is ListRepository.State.Error -> {
                Text(text = "Error")
            }
            is ListRepository.State.Success -> {
                val titleLanguage by component.titleLanguage.collectAsStateWithLifecycle(null)

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                    contentPadding = padding.merge(PaddingValues(16.dp)),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(current.medium.toList(), key = { it.id }) {
                        ListCard(
                            medium = it,
                            titleLanguage = titleLanguage,
                            modifier = Modifier.fillParentMaxWidth().height(150.dp),
                            onClick = component::details
                        )
                    }
                }
            }
        }
    }
}