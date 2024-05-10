package dev.datlag.aniflow.ui.navigation.screen.medium

import dev.datlag.aniflow.anilist.model.Character as Char
import kotlinx.serialization.Serializable

@Serializable
sealed class DialogConfig {

    @Serializable
    data class Character(
        val initial: Char
    ) : DialogConfig()

    @Serializable
    data object Edit : DialogConfig()
}