package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.DI
import org.kodein.di.instance

class FavoritesScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onDiscover: () -> Unit,
    private val onHome: () -> Unit,
) : FavoritesComponent, ComponentContext by componentContext {

    private val userHelper by instance<UserHelper>()
    val user = userHelper.user

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

    override fun viewDiscover() {
        onDiscover()
    }

    override fun viewHome() {
        onHome()
    }
}