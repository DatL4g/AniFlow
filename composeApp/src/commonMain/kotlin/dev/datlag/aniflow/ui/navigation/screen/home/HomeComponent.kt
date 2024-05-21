package dev.datlag.aniflow.ui.navigation.screen.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.trace.TraceRepository
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface HomeComponent : Component {
    val viewing: Flow<MediaType>
    val user: Flow<User?>
    val loggedIn: Flow<Boolean>
    val titleLanguage: Flow<TitleLanguage?>

    val airing: StateFlow<HomeAiringState>
    val trending: StateFlow<HomeDefaultState>
    val popularNow: StateFlow<HomeDefaultState>
    val popularNext: StateFlow<HomeDefaultState>

    val traceState: Flow<TraceRepository.State>

    val dialog: Value<ChildSlot<DialogConfig, DialogComponent>>

    fun viewProfile()
    fun viewAnime()
    fun viewManga()
    fun viewDiscover()
    fun viewFavorites()

    fun details(medium: Medium)
    fun trace(byteArray: ByteArray)
    fun clearTrace()
}