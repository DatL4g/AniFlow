package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.state.DiscoverListType
import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.common.animatePaddingAsState
import dev.datlag.aniflow.common.header
import dev.datlag.aniflow.common.icon
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.common.scrollUpVisible
import dev.datlag.aniflow.common.title
import dev.datlag.aniflow.other.rememberSearchBarState
import dev.datlag.aniflow.ui.custom.ErrorContent
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.MediumCard
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.discover.component.DiscoverSearchBar
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(component: DiscoverComponent) {
    val listState = rememberLazyGridState()
    val searchBarState = rememberSearchBarState()
    val discoverType by component.discoverType.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DiscoverSearchBar(
                searchBarState = searchBarState,
                component = component
            )
        },
        floatingActionButton = {
            val listTypeDialog = rememberUseCaseState()
            val loggedIn by component.loggedIn.collectAsStateWithLifecycle(false)
            val list = remember { DiscoverListType.entries.toList() }

            OptionDialog(
                state = listTypeDialog,
                selection = OptionSelection.Single(
                    options = list.map {
                        Option(
                            icon = IconSource(it.icon()),
                            titleText = stringResource(it.title()),
                            selected = it == discoverType,
                            disabled = (it is DiscoverListType.Recommendation && !loggedIn)
                        )
                    },
                    onSelectOption = { option, _ ->
                        component.listType(list[option])
                    }
                ),
                config = OptionConfig(
                    mode = DisplayMode.LIST
                ),
                header = Header.Default(
                    icon = IconSource(Icons.Rounded.FilterList),
                    title = stringResource(SharedRes.strings.discover)
                )
            )

            ExtendedFloatingActionButton(
                onClick = {
                    listTypeDialog.show()
                },
                expanded = listState.scrollUpVisible(),
                icon = {
                    Icon(
                        imageVector = discoverType.icon(),
                        contentDescription = stringResource(discoverType.title())
                    )
                },
                text = {
                    Text(text = stringResource(discoverType.title()))
                }
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
            is DiscoverState.Season.Loading, is DiscoverState.Recommended.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
                    )
                }
            }
            is DiscoverState.Failure -> {
                ErrorContent(
                    modifier = Modifier.fillMaxSize().padding(smoothPadding)
                )
            }
            is DiscoverState.Success -> {
                val titleLanguage by component.titleLanguage.collectAsStateWithLifecycle(null)

                LazyVerticalGrid(
                    state = listState,
                    modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                    contentPadding = smoothPadding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    columns = GridCells.Adaptive(120.dp)
                ) {
                    items(current.collection.toList(), key = { it.id }) {
                        MediumCard(
                            medium = it,
                            titleLanguage = titleLanguage,
                            modifier = Modifier.fillMaxWidth().aspectRatio(0.65F),
                            onClick = component::details
                        )
                    }
                }
            }
            else -> { }
        }
    }
}