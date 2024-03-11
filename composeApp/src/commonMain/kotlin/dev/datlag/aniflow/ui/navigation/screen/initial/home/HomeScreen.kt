package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier

@Composable
fun HomeScreen(component: HomeComponent) {
    val trendingState by component.trendingState.collectAsStateWithLifecycle()

    LaunchedEffect(trendingState) {
        Napier.e(trendingState.toString())
    }

    Text(text = "Hello World")
}