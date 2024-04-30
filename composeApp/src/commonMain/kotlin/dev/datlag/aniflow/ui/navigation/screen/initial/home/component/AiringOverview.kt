package dev.datlag.aniflow.ui.navigation.screen.initial.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.AiringQuery
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.shimmer
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AiringOverview(
    state: Flow<AiringTodayStateMachine.State>,
    titleLanguage: Flow<AppSettings.TitleLanguage?>,
    onClick: (Medium) -> Unit
) {
    val loadingState by state.collectAsStateWithLifecycle(StateSaver.Home.airingState)

    when (val reachedState = loadingState) {
        is AiringTodayStateMachine.State.Loading -> {
            Loading()
        }
        is AiringTodayStateMachine.State.Success -> {
            SuccessContent(
                data = reachedState.data.Page?.airingSchedulesFilterNotNull() ?: emptyList(),
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
        modifier = Modifier.fillMaxWidth().height(150.dp),
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
    data: List<AiringQuery.AiringSchedule>,
    titleLanguage: Flow<AppSettings.TitleLanguage?>,
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
                titleLanguageFlow = titleLanguage,
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