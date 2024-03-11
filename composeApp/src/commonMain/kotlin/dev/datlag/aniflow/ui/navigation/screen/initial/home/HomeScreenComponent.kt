package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.common.onRender
import org.kodein.di.DI

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : HomeComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        onRender {
            HomeScreen(this)
        }
    }

    override fun dismissContent() {

    }
}