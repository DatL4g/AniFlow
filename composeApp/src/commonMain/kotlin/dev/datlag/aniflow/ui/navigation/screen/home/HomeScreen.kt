package dev.datlag.aniflow.ui.navigation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.rounded.GetApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.maxkeppeker.sheets.core.models.base.Header
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
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.other.rememberImagePickerState
import dev.datlag.aniflow.trace.TraceRepository
import dev.datlag.aniflow.ui.custom.InstantAppContent
import dev.datlag.aniflow.ui.navigation.screen.component.CollapsingToolbar
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.aniflow.ui.navigation.screen.home.component.AllLoadingView
import dev.datlag.aniflow.ui.navigation.screen.home.component.DefaultOverview
import dev.datlag.aniflow.ui.navigation.screen.home.component.ScheduleOverview
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val listState = rememberLazyListState()
    val imagePicker = rememberImagePickerState {
        it?.let(component::trace)
    }

    val dialogState by component.dialog.subscribeAsState()

    dialogState.child?.instance?.render()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingToolbar(
                state = appBarState,
                scrollBehavior = scrollBehavior,
                userFlow = component.user,
                viewTypeFlow = component.viewing,
                onProfileClick = component::viewProfile,
                onAnimeClick = component::viewAnime,
                onMangaClick = component::viewManga
            )
        },
        floatingActionButton = {
            InstantAppContent(
                onInstantApp = { helper ->
                    ExtendedFloatingActionButton(
                        onClick = {
                            helper.showInstallPrompt()
                        },
                        expanded = listState.isScrollingUp() && listState.canScrollForward,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.GetApp,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(text = stringResource(SharedRes.strings.install))
                        }
                    )
                }
            ) {
                val traceState by component.traceState.collectAsStateWithLifecycle(TraceRepository.State.None)
                val results = remember(traceState) {
                    (traceState as? TraceRepository.State.Success)?.response?.combinedResults.orEmpty().toList()
                }

                val optionState = rememberUseCaseState(
                    visible = results.isNotEmpty(),
                    onCloseRequest = { component.clearTrace() },
                    onDismissRequest = { component.clearTrace() },
                    onFinishedRequest = { component.clearTrace() }
                )

                LaunchedEffect(results) {
                    if (results.isNotEmpty()) {
                        optionState.show()
                    }
                }

                OptionDialog(
                    state = optionState,
                    config = OptionConfig(
                        mode = DisplayMode.LIST
                    ),
                    header = Header.Custom { padding ->
                        Text(
                            modifier = Modifier.padding(padding.merge(PaddingValues(16.dp))).fillMaxWidth(),
                            text = "Matching Anime",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    selection = OptionSelection.Single(
                        options = results.map {
                            Option(
                                titleText = it.aniList.asMedium().preferred(null)
                            )
                        },
                        onSelectOption = { option, _ ->
                            component.details(results[option].aniList.asMedium())
                        }
                    )
                )

                ExtendedFloatingActionButton(
                    onClick = {
                        imagePicker.launch()
                    },
                    expanded = listState.isScrollingUp() && listState.canScrollForward,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.CameraEnhance,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(text = "Scan")
                    }
                )
            }
        },
        bottomBar = {
            HidingNavigationBar(
                visible = listState.isScrollingUp() && listState.canScrollForward,
                selected = NavigationBarState.Home,
                onDiscover = component::viewDiscover,
                onHome = { },
                onFavorites = component::viewFavorites
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
                val titleLanguage by component.titleLanguage.collectAsStateWithLifecycle(null)

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = LocalPadding(vertical = 16.dp)
                ) {
                    if (!isManga) {
                        item {
                            ScheduleOverview(
                                flow = component.airing,
                                titleLanguage = titleLanguage,
                                onMediumClick = component::details
                            )
                        }
                    }
                    item {
                        DefaultOverview(
                            title = "Trending",
                            flow = component.trending,
                            titleLanguage = titleLanguage,
                            onMediumClick = component::details
                        )
                    }
                    item {
                        DefaultOverview(
                            title = "Popular",
                            flow = component.popularNow,
                            titleLanguage = titleLanguage,
                            onMediumClick = component::details
                        )
                    }
                    if (!isManga) {
                        item {
                            DefaultOverview(
                                title = "Popular Next",
                                flow = component.popularNext,
                                titleLanguage = titleLanguage,
                                onMediumClick = component::details
                            )
                        }
                    }
                }
            }
        }
    }
}