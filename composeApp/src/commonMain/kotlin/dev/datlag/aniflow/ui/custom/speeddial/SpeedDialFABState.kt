package dev.datlag.aniflow.ui.custom.speeddial

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*

@Composable
fun rememberSpeedDialState(): SpeedDialFABState {
    val state = remember { SpeedDialFABState() }

    state.transition = updateTransition(targetState = state.currentState)

    return state
}

class SpeedDialFABState {

    var currentState by mutableStateOf<SpeedDialState>(SpeedDialState.Collapsed)
    var transition: Transition<SpeedDialState>? = null

    fun changeState() {
        currentState = if (transition?.currentState == SpeedDialState.Expanded
            || (transition?.isRunning == true && transition?.targetState == SpeedDialState.Expanded)) {
            SpeedDialState.Collapsed
        } else {
            SpeedDialState.Expanded
        }
    }

    fun collapse() {
        currentState = SpeedDialState.Collapsed
    }

    fun expand() {
        currentState = SpeedDialState.Expanded
    }
}