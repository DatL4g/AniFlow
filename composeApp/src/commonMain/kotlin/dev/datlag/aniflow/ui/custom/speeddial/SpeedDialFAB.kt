package dev.datlag.aniflow.ui.custom.speeddial

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@Composable
fun SpeedDialFAB(
    modifier: Modifier = Modifier,
    state: SpeedDialFABState,
    onClick: (SpeedDialFABState) -> Unit = { it.changeState() },
    iconRotation: Float = 45F,
    expanded: Boolean = false,
    shape: Shape = FloatingActionButtonDefaults.extendedFabShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    text: @Composable () -> Unit = { },
    icon: @Composable () -> Unit,
) {
    val rotation = if (iconRotation > 0F) {
        state.transition?.animateFloat(
            transitionSpec = {
                if (targetState == SpeedDialState.Expanded) {
                    spring(stiffness = Spring.StiffnessLow)
                } else {
                    spring(stiffness = Spring.StiffnessMedium)
                }
            },
            label = "",
            targetValueByState = {
                if (it == SpeedDialState.Expanded) iconRotation else 0F
            }
        )
    } else {
        null
    }

    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = {
            onClick(state)
        },
        expanded = expanded,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        icon = {
            Box(
                modifier = Modifier.rotate(rotation?.value ?: 0F),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        },
        text = {
            text()
        }
    )
}