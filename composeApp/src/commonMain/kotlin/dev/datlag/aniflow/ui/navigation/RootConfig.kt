package dev.datlag.aniflow.ui.navigation

import dev.datlag.aniflow.anilist.model.Medium
import kotlinx.serialization.Serializable

@Serializable
sealed class RootConfig {

    @Serializable
    data object Home : RootConfig()

    @Serializable
    data class Details(val medium: Medium) : RootConfig()
}