package dev.datlag.aniflow.ui.navigation.screen.initial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.ui.navigation.screen.initial.component.CompactScreen
import dev.datlag.aniflow.ui.navigation.screen.initial.component.ExpandedScreen
import dev.datlag.aniflow.ui.navigation.screen.initial.component.MediumScreen

@OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun InitialScreen(component: InitialComponent) {
    val haze = remember { HazeState() }

    CompositionLocalProvider(
        LocalHaze provides haze
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (calculateWindowSizeClass().widthSizeClass) {
                WindowWidthSizeClass.Compact -> CompactScreen(component)
                WindowWidthSizeClass.Medium -> MediumScreen(component)
                WindowWidthSizeClass.Expanded -> ExpandedScreen(component)
            }
        }
    }
}