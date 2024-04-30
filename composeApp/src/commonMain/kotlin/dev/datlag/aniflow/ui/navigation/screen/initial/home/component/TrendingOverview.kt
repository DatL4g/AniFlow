package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.shimmer
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TrendingOverview(
    state: Flow<TrendingAnimeStateMachine.State>,
    titleLanguage: Flow<AppSettings.TitleLanguage?>,
    onClick: (Medium) -> Unit,
) {
    val loadingState by state.collectAsStateWithLifecycle(StateSaver.Home.trendingState)

    when (val reachedState = loadingState) {
        is TrendingAnimeStateMachine.State.Loading -> {
            Loading()
        }
        is TrendingAnimeStateMachine.State.Success -> {
            SuccessContent(
                data = reachedState.data.Page?.mediaFilterNotNull() ?: emptyList(),
                titleLanguage = titleLanguage,
                onClick = onClick
            )
        }
        else -> {

        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessContent(
    data: List<TrendingQuery.Medium>,
    titleLanguage: Flow<AppSettings.TitleLanguage?>,
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
                titleLanguageFlow = titleLanguage,
                modifier = Modifier
                    .width(200.dp)
                    .height(280.dp)
                    .animateItemPlacement(),
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