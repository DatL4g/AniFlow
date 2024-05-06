package dev.datlag.aniflow.ui.navigation.screen.home

import dev.datlag.aniflow.anilist.AiringTodayRepository
import dev.datlag.aniflow.anilist.TrendingRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.trace.TraceRepository
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface HomeComponent : Component {
    val viewing: Flow<MediaType>

    val airing: Flow<AiringTodayRepository.State>
    val trending: Flow<CollectionState>
    val popularNow: Flow<CollectionState>
    val popularNext: Flow<CollectionState>

    val traceState: Flow<TraceRepository.State>

    fun viewProfile()
    fun viewAnime()
    fun viewManga()

    fun details(medium: Medium)
    fun trace(byteArray: ByteArray)
    fun clearTrace()
}