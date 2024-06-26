package dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings

import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.Flow
import dev.datlag.aniflow.settings.model.Color as SettingsColor
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar

interface SettingsComponent : DialogComponent {
    val user: Flow<User?>
    val isLoggedIn: Flow<Boolean>
    val loginUri: String

    val adultContent: Flow<Boolean>
    val selectedColor: Flow<SettingsColor?>
    val selectedTitleLanguage: Flow<SettingsTitle?>
    val selectedCharLanguage: Flow<SettingsChar?>

    fun changeAdultContent(value: Boolean)
    fun changeProfileColor(value: SettingsColor?)
    fun changeTitleLanguage(value: SettingsTitle?)
    fun changeCharLanguage(value: SettingsChar?)
    fun logout()
    fun nekos()
    fun about()
}