package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.SeasonQuery
import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.common.shimmer
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PopularSeasonOverview(
    state: StateFlow<SeasonState>,
    current: Boolean,
    onClick: (Medium) -> Unit,
) {
    val loadingState by state.collectAsStateWithLifecycle()

    when (val reachedState = loadingState) {
        is SeasonState.Loading -> {
            Loading()
        }
        is SeasonState.Success -> {
            SuccessContent(
                data = reachedState.data.Page?.mediaFilterNotNull() ?: emptyList(),
                current = current,
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
    data: List<SeasonQuery.Medium>,
    current: Boolean,
    onClick: (Medium) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (current) {
            StateSaver.List.Home.popularOverview
        } else {
            StateSaver.List.Home.popularNextOverview
        },
        initialFirstVisibleItemScrollOffset = if (current) {
            StateSaver.List.Home.popularOverviewOffset
        } else {
            StateSaver.List.Home.popularNextOverviewOffset
        },
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
            if (current) {
                StateSaver.List.Home.popularOverview = listState.firstVisibleItemIndex
                StateSaver.List.Home.popularOverviewOffset = listState.firstVisibleItemScrollOffset
            } else {
                StateSaver.List.Home.popularNextOverview = listState.firstVisibleItemIndex
                StateSaver.List.Home.popularNextOverviewOffset = listState.firstVisibleItemScrollOffset
            }
        }
    }
}
