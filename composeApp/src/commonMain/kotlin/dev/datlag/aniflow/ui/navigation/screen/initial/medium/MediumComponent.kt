package dev.datlag.aniflow.ui.navigation.screen.initial.medium

import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent

interface MediumComponent : ContentHolderComponent {
    val initialMedium: Medium

    fun back()
    override fun dismissContent() {
        back()
    }
}