package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character

import dev.datlag.aniflow.anilist.CharacterStateMachine
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CharacterComponent : DialogComponent {
    val initialChar: Character

    val state: StateFlow<CharacterStateMachine.State>

    val image: Flow<Character.Image>
    val name: Flow<Character.Name>
    val gender: Flow<String?>
    val bloodType: Flow<String?>
    val birthDate: Flow<Character.BirthDate?>
    val description: Flow<String?>
    val translatedDescription: StateFlow<String?>
    val isFavorite: Flow<Boolean>
    val isFavoriteBlocked: Flow<Boolean>

    fun descriptionTranslation(text: String?)
    fun retry()
    fun toggleFavorite()
}