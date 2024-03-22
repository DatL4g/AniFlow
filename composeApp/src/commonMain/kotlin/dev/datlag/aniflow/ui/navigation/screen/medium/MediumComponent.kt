package dev.datlag.aniflow.ui.navigation.screen.medium

import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import kotlinx.coroutines.flow.StateFlow

interface MediumComponent : ContentHolderComponent {
    val initialMedium: Medium

    val bannerImage: StateFlow<String?>
    val coverImage: StateFlow<Medium.CoverImage>
    val title: StateFlow<Medium.Title>
    val description: StateFlow<String?>
    val genres: StateFlow<Set<String>>

    val format: StateFlow<MediaFormat>
    val episodes: StateFlow<Int>
    val status: StateFlow<MediaStatus>

    fun back()
    override fun dismissContent() {
        back()
    }
}