package dev.datlag.aniflow.ui.navigation.screen.favorites

import dev.datlag.aniflow.anilist.ListRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FavoritesComponent : Component {
    val listState: StateFlow<ListRepository.State>
    val titleLanguage: Flow<TitleLanguage?>

    fun viewDiscover()
    fun viewHome()

    fun details(medium: Medium)
}