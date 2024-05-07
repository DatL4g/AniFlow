package dev.datlag.aniflow.ui.navigation.screen.wallpaper

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState

@Composable
fun WallpaperScreen(component: WallpaperComponent) {
    Scaffold(
        bottomBar = {
            HidingNavigationBar(
                visible = true,
                selected = NavigationBarState.Wallpaper,
                onWallpaper = { },
                onHome = component::viewHome,
                onFavorites = component::viewFavorites
            )
        }
    ) {

    }
}