package dev.datlag.aniflow.ui.navigation.screen.home

import kotlinx.serialization.Serializable

@Serializable
sealed class DialogConfig {

    @Serializable
    data object Settings : DialogConfig()
}