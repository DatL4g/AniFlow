package dev.datlag.aniflow.ui.navigation.screen.medium

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.MediumStateMachine
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.StateFlow

interface MediumComponent : ContentHolderComponent {
    val initialMedium: Medium

    val mediumState: StateFlow<MediumStateMachine.State>
    val bannerImage: StateFlow<String?>
    val coverImage: StateFlow<Medium.CoverImage>
    val title: StateFlow<Medium.Title>
    val description: StateFlow<String?>
    val translatedDescription: StateFlow<String?>
    val genres: StateFlow<Set<String>>

    val format: StateFlow<MediaFormat>
    val episodes: StateFlow<Int>
    val duration: StateFlow<Int>
    val status: StateFlow<MediaStatus>

    val rated: StateFlow<Medium.Ranking?>
    val popular: StateFlow<Medium.Ranking?>
    val score: StateFlow<Int?>

    val characters: StateFlow<Set<Character>>
    val rating: StateFlow<Int>
    val alreadyAdded: StateFlow<Boolean>
    val trailer: StateFlow<Medium.Trailer?>
    val isFavorite: StateFlow<Boolean>
    val isFavoriteBlocked: StateFlow<Boolean>

    val bsAvailable: Boolean

    val dialog: Value<ChildSlot<DialogConfig, DialogComponent>>

    fun back()
    override fun dismissContent() {
        back()
    }
    fun rate(onLoggedIn: () -> Unit)
    fun rate(value: Int)
    fun descriptionTranslation(text: String?)
    fun showCharacter(character: Character)
    fun toggleFavorite()
}