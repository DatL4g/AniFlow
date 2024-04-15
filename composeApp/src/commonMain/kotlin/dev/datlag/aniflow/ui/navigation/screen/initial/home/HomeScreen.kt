package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.chrisbanes.haze.haze
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.common.plus
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.AiringOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.PopularSeasonOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.TrendingOverview
import dev.datlag.aniflow.ui.navigation.screen.initial.model.FABConfig

@Composable
fun HomeScreen(component: HomeComponent) {
    MainView(component, Modifier.fillMaxWidth())
}

@Composable
private fun MainView(component: HomeComponent, modifier: Modifier = Modifier) {
    val padding = PaddingValues(vertical = 16.dp)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = StateSaver.List.homeOverview,
        initialFirstVisibleItemScrollOffset = StateSaver.List.homeOverviewOffset
    )

    LaunchedEffect(listState) {
        FABConfig.state.value = FABConfig.Scan(
            listState = listState,
            onClick = { }
        )
    }

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
                current = true,
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