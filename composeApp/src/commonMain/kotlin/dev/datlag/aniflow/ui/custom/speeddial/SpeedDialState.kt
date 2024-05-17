package dev.datlag.aniflow.ui.custom.speeddial

import kotlinx.serialization.Serializable

@Serializable
sealed interface SpeedDialState {
    @Serializable
    data object Collapsed : SpeedDialState

    @Serializable
    data object Expanded : SpeedDialState
}