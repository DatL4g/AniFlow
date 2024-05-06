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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.isScrollingUp
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HidingNavigationBar(
    visible: Boolean,
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
                onClick = { },
                selected = true,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(SharedRes.strings.home))
                }
            )
            NavigationBarItem(
                onClick = { },
                selected = false,
                icon = {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
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