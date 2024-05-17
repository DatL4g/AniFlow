package dev.datlag.aniflow.ui.custom.speeddial

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale

@Composable
fun AnimatedSmallFABWithLabel(
    state: SpeedDialFABState,
    showLabel: Boolean,
    modifier: Modifier = Modifier,
    labelContent: @Composable () -> Unit = { },
    fabContent: @Composable () -> Unit,
) {
    val alpha = state.transition?.animateFloat(
        transitionSpec = {
            tween(durationMillis = 50)
        },
        targetValueByState = {
            if (it == SpeedDialState.Expanded) 1F else 0F
        }
    )
    val scale = state.transition?.animateFloat(
        targetValueByState = {
            if (it == SpeedDialState.Expanded) 1F else 0F
        }
    )

    Row(
        modifier = modifier
            .alpha(animateFloatAsState((alpha?.value ?: 0F)).value)
            .scale(animateFloatAsState(scale?.value ?: 0F).value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = showLabel,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut()
        ) {
            labelContent.invoke()
        }
        fabContent.invoke()
    }
}