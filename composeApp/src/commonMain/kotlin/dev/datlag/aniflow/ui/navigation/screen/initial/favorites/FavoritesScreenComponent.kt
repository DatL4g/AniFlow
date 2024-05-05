package dev.datlag.aniflow.ui.navigation.screen.initial.favorites

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.common.onRender
import org.kodein.di.DI

class FavoritesScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : FavoritesComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        onRender {
            FavoritesScreen(this)
        }
    }
}