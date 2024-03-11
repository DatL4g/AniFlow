package dev.datlag.aniflow.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class RootConfig {

    @Serializable
    data object Home : RootConfig()
}