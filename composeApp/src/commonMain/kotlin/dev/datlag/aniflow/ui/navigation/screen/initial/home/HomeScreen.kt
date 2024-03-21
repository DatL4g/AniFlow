package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    when (calculateWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Expanded -> ExpandedView(component)
        else -> DefaultView(component)
    }
}

@Composable
private fun DefaultView(component: HomeComponent) {
    val childState by component.child.subscribeAsState()

    childState.child?.instance?.render() ?: MainView(component, Modifier.fillMaxWidth())
}

@Composable
private fun ExpandedView(component: HomeComponent) {
    val childState by component.child.subscribeAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val modifier = if (childState.child?.configuration != null) {
            Modifier.widthIn(max = 650.dp)
        } else {
            Modifier.fillMaxWidth()
        }
        MainView(component, modifier)

        childState.child?.instance?.let {
            Box(modifier = Modifier.weight(2F)) {
                it.render()
            }
        }
    }
}

@Composable
private fun MainView(component: HomeComponent, modifier: Modifier = Modifier) {
    val padding = PaddingValues(vertical = 16.dp)
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.haze(state = LocalHaze.current),
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
                state = component.airingState,
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
                onClick = component::details
            )
        }
    }
}