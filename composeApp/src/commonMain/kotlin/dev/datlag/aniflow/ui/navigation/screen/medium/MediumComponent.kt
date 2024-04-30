package dev.datlag.aniflow.ui.navigation.screen.medium

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.MediumStateMachine
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MediumComponent : ContentHolderComponent {
    val initialMedium: Medium
    val titleLanguage: Flow<AppSettings.TitleLanguage?>

    val mediumState: StateFlow<MediumStateMachine.State>
    val isAdult: Flow<Boolean>
    val isAdultAllowed: Flow<Boolean>

    val bannerImage: Flow<String?>
    val coverImage: Flow<Medium.CoverImage>
    val title: Flow<Medium.Title>
    val description: Flow<String?>
    val translatedDescription: StateFlow<String?>
    val genres: Flow<Set<String>>

    val format: Flow<MediaFormat>
    val episodes: Flow<Int>
    val duration: Flow<Int>
    val status: Flow<MediaStatus>

    val rated: Flow<Medium.Ranking?>
    val popular: Flow<Medium.Ranking?>
    val score: Flow<Int?>

    val characters: Flow<Set<Character>>
    val rating: Flow<Int>
    val alreadyAdded: Flow<Boolean>
    val trailer: Flow<Medium.Trailer?>
    val isFavorite: Flow<Boolean>
    val isFavoriteBlocked: Flow<Boolean>
    val siteUrl: Flow<String>

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