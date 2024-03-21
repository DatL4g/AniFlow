package dev.datlag.aniflow.ui.navigation.screen.initial.home

import dev.datlag.aniflow.anilist.model.Medium
import kotlinx.serialization.Serializable

@Serializable
sealed class HomeConfig {
    @Serializable
    data class Details(
        val medium: Medium
    ) : HomeConfig()
}