package dev.datlag.aniflow.ui.navigation.screen.discover

import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface DiscoverComponent : Component {
    val loggedIn: Flow<Boolean>

    val initialSearchValue: String?
    val type: Flow<MediaType>
    val searchResult: Flow<CollectionState>

    fun viewHome()
    fun viewList()
    fun details(medium: Medium)

    fun search(query: String)
    fun toggleView()
}