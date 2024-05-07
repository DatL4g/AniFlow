package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.common.onRender
import org.kodein.di.DI

class FavoritesScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onWallpaper: () -> Unit,
    private val onHome: () -> Unit,
) : FavoritesComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze
        ) {
            onRender {
                FavoritesScreen(this)
            }
        }
    }

    override fun viewWallpaper() {
        onWallpaper()
    }

    override fun viewHome() {
        onHome()
    }
}