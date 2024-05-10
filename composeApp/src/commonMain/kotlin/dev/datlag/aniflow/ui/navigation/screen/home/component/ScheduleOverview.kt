package dev.datlag.aniflow.ui.navigation.screen.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.anilist.AiringTodayRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.ui.navigation.screen.home.component.airing.AiringCard
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleOverview(
    flow: Flow<AiringTodayRepository.State>,
    onMediumClick: (Medium) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val state by flow.collectAsStateWithLifecycle(AiringTodayRepository.State.None)

        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        when (val current = state) {
            is AiringTodayRepository.State.None -> {
                Loading()
            }
            is AiringTodayRepository.State.Success -> {
                val listState = rememberLazyListState(
                    initialFirstVisibleItemIndex = StateSaver.List.Home.airingOverview,
                    initialFirstVisibleItemScrollOffset = StateSaver.List.Home.airingOverviewOffset
                )

                LazyRow(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    flingBehavior = rememberSnapFlingBehavior(listState),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(current.collection.toList()) {
                        AiringCard(
                            airing = it,
                            titleLanguage = null,
                            modifier = Modifier
                                .height(150.dp)
                                .fillParentMaxWidth(fraction = 0.9F)
                                .animateItemPlacement(),
                            onClick = onMediumClick
                        )
                    }
                }
            }
            is AiringTodayRepository.State.Error -> {
                Text("Could not load Schedule")
            }
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