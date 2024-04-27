package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.AiringQuery
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.shimmer
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AiringOverview(
    state: StateFlow<AiringTodayStateMachine.State>,
    onClick: (Medium) -> Unit
) {
    val loadingState by state.collectAsStateWithLifecycle()

    when (val reachedState = loadingState) {
        is AiringTodayStateMachine.State.Loading -> {
            Loading()
        }
        is AiringTodayStateMachine.State.Success -> {
            SuccessContent(
                data = reachedState.data.Page?.airingSchedulesFilterNotNull() ?: emptyList(),
                onClick = onClick
            )
        }
        else -> {

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Loading() {
    val state = rememberLazyListState()

    LazyRow(
        state = state,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        flingBehavior = rememberSnapFlingBehavior(state),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        repeat(5) {
            item {
                Box(
                    modifier = Modifier.width(300.dp).height(150.dp).shimmer(CardDefaults.shape)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessContent(
    data: List<AiringQuery.AiringSchedule>,
    onClick: (Medium) -> Unit
) {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = StateSaver.List.Home.airingOverview,
        initialFirstVisibleItemScrollOffset = StateSaver.List.Home.airingOverviewOffset
    )

    LazyRow(
        state = state,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        flingBehavior = rememberSnapFlingBehavior(state),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(data, key = { it.episode to it.media?.id }) { media ->
            AiringCard(
                airing = media,
                modifier = Modifier
                    .height(150.dp)
                    .fillParentMaxWidth(fraction = 0.9F)
                    .animateItemPlacement(),
                onClick = onClick
            )
        }
    }

    DisposableEffect(state) {
        onDispose {
            StateSaver.List.Home.airingOverview = state.firstVisibleItemIndex
            StateSaver.List.Home.airingOverviewOffset = state.firstVisibleItemScrollOffset
        }
    }
}