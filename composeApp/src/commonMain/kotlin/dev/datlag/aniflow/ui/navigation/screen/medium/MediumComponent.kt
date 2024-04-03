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
    val duration: StateFlow<Int>
    val status: StateFlow<MediaStatus>

    val rated: StateFlow<Medium.Ranking?>
    val popular: StateFlow<Medium.Ranking?>
    val score: StateFlow<Int?>

    val characters: StateFlow<Set<Medium.Character>>
    val rating: StateFlow<Int>
    val trailer: StateFlow<Medium.Full.Trailer?>

    fun back()
    override fun dismissContent() {
        back()
    }
    fun rate(onLoggedIn: () -> Unit)
    fun rate(value: Int)
}