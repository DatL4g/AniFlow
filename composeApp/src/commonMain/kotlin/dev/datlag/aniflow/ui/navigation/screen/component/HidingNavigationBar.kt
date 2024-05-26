package dev.datlag.aniflow.ui.navigation.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.other.MaterialSymbols
import dev.datlag.aniflow.ui.navigation.RootConfig
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HidingNavigationBar(
    visible: Boolean,
    selected: NavigationBarState,
    loggedIn: Flow<Boolean>,
    listClickable: Boolean = false,
    onDiscover: () -> Unit,
    onHome: () -> Unit,
    onList: (isLoggedIn: Boolean) -> Unit
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
            val isDiscover = remember(selected) {
                selected is NavigationBarState.Discover
            }
            val isHome = remember(selected) {
                selected is NavigationBarState.Home
            }
            val isList = remember(selected) {
                selected is NavigationBarState.Favorite
            }
            val isLoggedIn by loggedIn.collectAsStateWithLifecycle(false)

            NavigationBarItem(
                onClick = {
                    if (!isDiscover) {
                        onDiscover()
                    }
                },
                selected = isDiscover,
                icon = {
                    Icon(
                        imageVector = selected.discoverIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(SharedRes.strings.discover))
                }
            )
            NavigationBarItem(
                onClick = {
                    if (!isHome) {
                        onHome()
                    }
                },
                selected = isHome,
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
                    if (!isList) {
                        onList(isLoggedIn)
                    }
                },
                selected = isList,
                enabled = isLoggedIn || listClickable,
                icon = {
                    Icon(
                        imageVector = selected.favoriteIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(SharedRes.strings.list))
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

    val discoverIcon: ImageVector
        get() = when (this) {
            is Discover -> selectedIcon
            else -> Discover.unselectedIcon
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
    data object Discover : NavigationBarState {
        override val unselectedIcon: ImageVector
            get() = Icons.Rounded.Search
    }

    @Serializable
    data object Home : NavigationBarState {
        override val unselectedIcon: ImageVector
            get() = MaterialSymbols.Rounded.Home

        override val selectedIcon: ImageVector
            get() = MaterialSymbols.Filled.Home
    }

    @Serializable
    data object Favorite : NavigationBarState {
        override val unselectedIcon: ImageVector
            get() = Icons.Rounded.Checklist
    }
}