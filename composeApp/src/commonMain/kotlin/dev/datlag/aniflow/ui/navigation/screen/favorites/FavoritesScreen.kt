package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import kotlinx.coroutines.flow.flowOf

@Composable
fun FavoritesScreen(component: FavoritesComponent) {
    Scaffold(
        bottomBar = {
            HidingNavigationBar(
                visible = true,
                selected = NavigationBarState.Favorite,
                loggedIn = flowOf(true),
                onDiscover = component::viewDiscover,
                onHome = component::viewHome,
                onFavorites = { }
            )
        }
    ) {

    }
}