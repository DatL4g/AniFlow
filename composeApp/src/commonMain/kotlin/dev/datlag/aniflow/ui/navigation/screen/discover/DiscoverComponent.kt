package dev.datlag.aniflow.ui.navigation.screen.discover

import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.anilist.state.SearchState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DiscoverComponent : Component {
    val loggedIn: Flow<Boolean>

    val initialSearchValue: String?
    val type: Flow<MediaType>
    val searchResult: StateFlow<SearchState>

    val state: StateFlow<DiscoverState>

    fun viewHome()
    fun viewList()
    fun details(medium: Medium)

    fun search(query: String)
    fun toggleView()
}