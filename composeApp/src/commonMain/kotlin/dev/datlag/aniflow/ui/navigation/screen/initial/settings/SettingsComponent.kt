package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface SettingsComponent : Component {
    val adultContent: Flow<Boolean>

    fun changeAdultContent(value: Boolean)
}