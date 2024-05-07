package dev.datlag.aniflow.ui.navigation.screen.wallpaper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.common.onRender
import org.kodein.di.DI

class WallpaperScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onHome: () -> Unit,
    private val onFavorites: () -> Unit,
) : WallpaperComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze
        ) {
            onRender {
                WallpaperScreen(this)
            }
        }
    }

    override fun viewHome() {
        onHome()
    }

    override fun viewFavorites() {
        onFavorites()
    }
}