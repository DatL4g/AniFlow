package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.shimmer
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TrendingOverview(
    state: StateFlow<TrendingAnimeStateMachine.State>,
    onClick: (Medium) -> Unit,
) {
    val loadingState by state.collectAsStateWithLifecycle()

    when (val reachedState = loadingState) {
        is TrendingAnimeStateMachine.State.Loading -> {
            Loading()
        }
        is TrendingAnimeStateMachine.State.Success -> {
            SuccessContent(
                data = reachedState.data.Page?.mediaFilterNotNull() ?: emptyList(),
                onClick = onClick
            )
        }
        else -> {

        }
    }
}

@Composable
private fun Loading() {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        repeat(5) {
            item {
                Box(
                    modifier = Modifier.width(200.dp).height(280.dp).shimmer(CardDefaults.shape)
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    data: List<TrendingQuery.Medium>,
    onClick: (Medium) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = StateSaver.List.Home.trendingOverview,
        initialFirstVisibleItemScrollOffset = StateSaver.List.Home.trendingOverviewOffset
    )

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(data, key = { _, it -> it.id }) { _, medium ->
            MediumCard(
                medium = Medium(medium),
                modifier = Modifier.width(200.dp).height(280.dp),
                onClick = onClick
            )
        }
    }

    DisposableEffect(listState) {
        onDispose {
            StateSaver.List.Home.trendingOverview = listState.firstVisibleItemIndex
            StateSaver.List.Home.trendingOverviewOffset = listState.firstVisibleItemScrollOffset
        }
    }
}