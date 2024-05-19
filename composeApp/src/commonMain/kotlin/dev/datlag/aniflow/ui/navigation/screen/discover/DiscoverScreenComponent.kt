package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import kotlinx.coroutines.flow.Flow
import org.kodein.di.DI
import org.kodein.di.instance

class DiscoverScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onHome: () -> Unit,
    private val onList: () -> Unit,
) : DiscoverComponent, ComponentContext by componentContext {

    private val userHelper by instance<UserHelper>()
    override val loggedIn: Flow<Boolean> = userHelper.isLoggedIn

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze
        ) {
            onRender {
                DiscoverScreen(this)
            }
        }
    }

    override fun viewHome() {
        onHome()
    }

    override fun viewList() {
        onList()
    }
}