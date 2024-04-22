package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character

import dev.datlag.aniflow.anilist.CharacterStateMachine
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.StateFlow

interface CharacterComponent : DialogComponent {
    val initialChar: Character

    val state: StateFlow<CharacterStateMachine.State>

    val image: StateFlow<Character.Image>
    val name: StateFlow<Character.Name>
    val gender: StateFlow<String?>
    val bloodType: StateFlow<String?>
    val birthDate: StateFlow<Character.BirthDate?>
    val description: StateFlow<String?>
    val translatedDescription: StateFlow<String?>

    fun descriptionTranslation(text: String?)
    fun retry()
}