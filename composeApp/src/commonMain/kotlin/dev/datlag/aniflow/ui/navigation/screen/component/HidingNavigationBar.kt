package dev.datlag.aniflow.ui.navigation.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.ui.navigation.RootConfig
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HidingNavigationBar(
    visible: Boolean,
    selected: NavigationBarState,
    onWallpaper: () -> Unit,
    onHome: () -> Unit,
    onFavorites: () -> Unit
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = {
                with(density) { it.dp.roundToPx() }
            },
            animationSpec = tween(
                easing = LinearOutSlowInEasing,
                durationMillis = 500
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                easing = LinearOutSlowInEasing,
                durationMillis = 500
            )
        )
    ) {
        NavigationBar(
            modifier = Modifier.hazeChild(
                state = LocalHaze.current,
                style = HazeMaterials.thin(NavigationBarDefaults.containerColor)
            ).fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.contentColorFor(NavigationBarDefaults.containerColor)
        ) {
            NavigationBarItem(
                onClick = {
                    if (selected !is NavigationBarState.Wallpaper) {
                        onWallpaper()
                    }
                },
                selected = selected is NavigationBarState.Wallpaper,
                icon = {
                    Icon(
                        imageVector = selected.wallpaperIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Wallpapers")
                }
            )
            NavigationBarItem(
                onClick = {
                    if (selected !is NavigationBarState.Home) {
                        onHome()
                    }
                },
                selected = selected is NavigationBarState.Home,
                icon = {
                    Icon(
                        imageVector = selected.homeIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(SharedRes.strings.home))
                }
            )
            NavigationBarItem(
                onClick = {
                    if (selected !is NavigationBarState.Favorite) {
                        onFavorites()
                    }
                },
                selected = selected is NavigationBarState.Favorite,
                icon = {
                    Icon(
                        imageVector = selected.favoriteIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(SharedRes.strings.favorites))
                }
            )
        }
    }
}

@Serializable
sealed interface NavigationBarState {
    @Transient
    val unselectedIcon: ImageVector

    @Transient
    val selectedIcon: ImageVector
        get() = unselectedIcon

    val wallpaperIcon: ImageVector
        get() = when (this) {
            is Wallpaper -> selectedIcon
            else -> Wallpaper.unselectedIcon
        }

    val homeIcon: ImageVector
        get() = when (this) {
            is Home -> selectedIcon
            else -> Home.unselectedIcon
        }

    val favoriteIcon: ImageVector
        get() = when (this) {
            is Favorite -> selectedIcon
            else -> Favorite.unselectedIcon
        }

    @Serializable
    data object Wallpaper : NavigationBarState {
        override val unselectedIcon: ImageVector
            get() = Icons.Rounded.Wallpaper
    }

    @Serializable
    data object Home : NavigationBarState {
        override val unselectedIcon: ImageVector
            get() = Icons.Outlined.Home

        override val selectedIcon: ImageVector
            get() = Icons.Rounded.Home
    }

    @Serializable
    data object Favorite : NavigationBarState {
        override val unselectedIcon: ImageVector
            get() = Icons.Rounded.FavoriteBorder

        override val selectedIcon: ImageVector
            get() = Icons.Rounded.Favorite
    }
}