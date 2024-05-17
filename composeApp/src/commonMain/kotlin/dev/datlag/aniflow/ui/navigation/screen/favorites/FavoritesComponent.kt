package dev.datlag.aniflow.ui.navigation.screen.favorites

import dev.datlag.aniflow.anilist.ListRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FavoritesComponent : Component {
    val listState: StateFlow<ListRepository.State>
    val titleLanguage: Flow<TitleLanguage?>
    val type: Flow<MediaType>
    val status: Flow<MediaListStatus>

    fun viewDiscover()
    fun viewHome()

    fun details(medium: Medium)
    fun increase(medium: Medium, progress: Int)

    fun toggleView()
    fun setStatus(status: MediaListStatus)
}