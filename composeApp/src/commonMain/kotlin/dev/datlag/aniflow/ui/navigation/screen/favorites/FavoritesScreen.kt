package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.ListState
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.ui.custom.ErrorContent
import dev.datlag.aniflow.ui.custom.InfiniteListHandler
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.favorites.component.ListCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun FavoritesScreen(component: FavoritesComponent) {
    val listState = rememberLazyListState()
    val type by component.type.collectAsStateWithLifecycle(MediaType.UNKNOWN__)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(SharedRes.strings.app_name))
                },
                actions = {

                    IconButton(
                        onClick = component::toggleView,
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
            val status by component.status.collectAsStateWithLifecycle(MediaListStatus.UNKNOWN__)
            val statusDialog = rememberUseCaseState()
            val options = remember {
                setOf(
                    MediaListStatus.UNKNOWN__,
                    *MediaListStatus.entries.toTypedArray()
                ).toList()
            }

            OptionDialog(
                state = statusDialog,
                selection = OptionSelection.Single(
                    options = options.map {
                        Option(
                            icon = IconSource(it.icon(Icons.Rounded.ClearAll)),
                            titleText = stringResource(it.stringRes(type, SharedRes.strings.all)),
                            selected = status == it
                        )
                    },
                    onSelectOption = { option, _ ->
                        component.setStatus(options[option])
                    }
                ),
                config = OptionConfig(
                    mode = DisplayMode.LIST
                ),
                header = Header.Default(
                    icon = IconSource(Icons.Rounded.FilterList),
                    title = stringResource(SharedRes.strings.filter)
                )
            )

            ExtendedFloatingActionButton(
                onClick = {
                    statusDialog.show()
                },
                expanded = listState.scrollUpVisible(),
                icon = {
                    Icon(
                        imageVector = status.icon(Icons.Rounded.FilterList),
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = stringResource(status.stringRes(type, SharedRes.strings.all)))
                }
            )
        },
        bottomBar = {
            HidingNavigationBar(
                visible = listState.scrollUpVisible(),
                selected = NavigationBarState.Favorite,
                loggedIn = flowOf(true),
                onDiscover = component::viewDiscover,
                onHome = component::viewHome,
                onFavorites = { }
            )
        }
    ) { padding ->
        val state by component.listState.collectAsStateWithLifecycle()

        ListData(
            state = state,
            listState = listState,
            padding = padding,
            titleLanguage = null,
            onClick = component::details,
            onIncrease = component::increase,
            onLoadMore = component::nextPage
        )
    }
}

@Composable
private fun ListData(
    state: ListState,
    listState: LazyListState,
    padding: PaddingValues,
    titleLanguage: TitleLanguage?,
    onClick: (Medium) -> Unit,
    onIncrease: (Medium, Int) -> Unit,
    onLoadMore: suspend () -> Unit
) {
    val collection = remember(state) { state.collection }

    if (collection.isNotEmpty()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
            contentPadding = padding.merge(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(collection.toList(), key = { it.id }) {
                ListCard(
                    medium = it,
                    titleLanguage = titleLanguage,
                    modifier = Modifier.fillParentMaxWidth().height(150.dp),
                    onClick = onClick,
                    onIncrease = onIncrease
                )
            }
            if (state is ListState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
                        )
                    }
                }
            }
        }

        InfiniteListHandler(
            listState = listState,
            canLoadMore = state.hasNextPage,
            onLoadMore = onLoadMore
        )
    } else {
        when (state) {
            is ListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.2F).clip(CircleShape)
                    )
                }
            }
            is ListState.Failure -> {
                ErrorContent(
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> { }
        }
    }
}