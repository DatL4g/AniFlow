package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionDetails
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.asMedium
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.common.preferred
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.other.rememberImagePickerState
import dev.datlag.aniflow.trace.TraceStateMachine
import dev.datlag.aniflow.trace.model.SearchResponse
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.AiringOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.PopularSeasonOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.TrendingOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.model.FABConfig
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun HomeScreen(component: HomeComponent) {
    val isAllLoading by StateSaver.Home.isAllLoading.collectAsStateWithLifecycle(StateSaver.Home.currentAllLoading)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MainView(component, Modifier.fillMaxWidth())

        if (isAllLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainView(component: HomeComponent, modifier: Modifier = Modifier) {
    val padding = PaddingValues(vertical = 16.dp)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = StateSaver.List.homeOverview,
        initialFirstVisibleItemScrollOffset = StateSaver.List.homeOverviewOffset
    )
    val imagePicker = rememberImagePickerState {
        it?.let(component::trace)
    }
    val traceState by component.traceState.collectAsStateWithLifecycle(TraceStateMachine.State.Waiting)
    val titleLanguage by component.titleLanguage.collectAsStateWithLifecycle(null)

    LaunchedEffect(listState, traceState) {
        FABConfig.state.value = FABConfig.Scan(
            listState = listState,
            loading = traceState.isLoading,
            onClick = {
                imagePicker.launch()
            }
        )
    }

    when (val current = traceState) {
        is TraceStateMachine.State.Success -> {
            val results = remember(traceState) {
                current.response.combinedResults.sortedWith(
                    compareByDescending<SearchResponse.CombinedResult> {
                        it.maxSimilarity
                    }.thenByDescending {
                        it.avgSimilarity
                    }
                )
            }
            val useCase = rememberUseCaseState(visible = results.isNotEmpty())

            OptionDialog(
                state = useCase,
                selection = OptionSelection.Single(
                    options = results.map {
                        Option(
                            titleText = it.aniList.asMedium().title.preferred(titleLanguage),
                            details = OptionDetails(
                                title = stringResource(SharedRes.strings.similarity_title),
                                body = if (it.isSingle) {
                                    stringResource(SharedRes.strings.similarity_text_single, it.avgPercentage)
                                } else {
                                    stringResource(SharedRes.strings.similarity_text_max_avg, it.maxPercentage, it.avgPercentage)
                                }
                            )
                        )
                    },
                    onSelectOption = { index, _ ->
                        component.details(results[index].aniList.asMedium())
                    }
                ),
                config = OptionConfig(
                    mode = DisplayMode.LIST
                )
            )
        }
        else -> { }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.haze(state = LocalHaze.current),
        contentPadding = LocalPaddingValues.current?.plus(padding) ?: padding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val type by component.viewing.subscribeAsState()
            val isManga = remember(type) {
                type == MediaType.MANGA
            }

            Box(
                modifier = Modifier.fillParentMaxWidth().height(200.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.3F), CircleShape),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(
                        onClick = {
                            component.viewAnime()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = if (isManga) {
                                LocalContentColor.current
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            component.viewManga()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = if (isManga) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current
                            }
                        )
                    }
                }
            }
        }
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Schedule",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            AiringOverview(
                state = component.airingState,
                titleLanguage = titleLanguage,
                onClick = component::details
            )
        }
        item {
            Text(
                text = "Trending",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            TrendingOverview(
                state = component.trendingState,
                titleLanguage = titleLanguage,
                onClick = component::details
            )
        }
        item {
            Text(
                text = "Popular This Season",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            PopularSeasonOverview(
                state = component.popularSeasonState,
                current = true,
                titleLanguage = titleLanguage,
                onClick = component::details,
            )
        }
        item {
            Text(
                text = "Upcoming Next Season",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            PopularSeasonOverview(
                state = component.popularNextSeasonState,
                current = false,
                titleLanguage = titleLanguage,
                onClick = component::details
            )
        }
    }

    DisposableEffect(listState) {
        onDispose {
            StateSaver.List.homeOverview = listState.firstVisibleItemIndex
            StateSaver.List.homeOverviewOffset = listState.firstVisibleItemScrollOffset
            FABConfig.state.value = null
        }
    }
}