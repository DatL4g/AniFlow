package dev.datlag.aniflow.ui.navigation.screen.discover

import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface DiscoverComponent : Component {
    val loggedIn: Flow<Boolean>

    fun viewHome()
    fun viewList()
}