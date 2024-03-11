package dev.datlag.aniflow.ui.navigation.screen.initial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze

@Composable
fun InitialScreen(component: InitialComponent) {
    val haze = remember { HazeState() }

    CompositionLocalProvider(
        LocalHaze provides haze
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

        }
    }
}