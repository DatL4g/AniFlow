package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface SettingsComponent : Component {
    val user: Flow<User?>
    val adultContent: Flow<Boolean>
    val selectedColor: Flow<AppSettings.Color?>
    val selectedTitleLanguage: Flow<AppSettings.TitleLanguage?>

    fun changeAdultContent(value: Boolean)
    fun changeProfileColor(value: AppSettings.Color?)
    fun changeTitleLanguage(value: AppSettings.TitleLanguage?)
}