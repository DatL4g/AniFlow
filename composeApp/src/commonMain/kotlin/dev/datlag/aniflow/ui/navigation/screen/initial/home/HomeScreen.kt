package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.AiringOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.MediumCard
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.PopularSeasonOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.TrendingOverview
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier

@Composable
fun HomeScreen(component: HomeComponent) {
    val padding = PaddingValues(vertical = 16.dp)
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
        contentPadding = LocalPaddingValues.current?.plus(padding) ?: padding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                state = component.airingState
            ) {

            }
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
                state = component.trendingState
            ) {

            }
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
                state = component.popularSeasonState
            ) {

            }
        }
    }
}