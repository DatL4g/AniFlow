package dev.datlag.aniflow.ui.navigation.screen.initial

import kotlinx.serialization.Serializable

@Serializable
sealed class View {
    @Serializable
    data object Home : View()
}