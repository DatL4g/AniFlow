package dev.datlag.aniflow.ui.navigation.screen.initial.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.trace.TraceStateMachine
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface HomeComponent : ContentHolderComponent {
    val airingState: StateFlow<AiringTodayStateMachine.State>
    val trendingState: StateFlow<TrendingAnimeStateMachine.State>
    val popularSeasonState: StateFlow<SeasonState>
    val popularNextSeasonState: StateFlow<SeasonState>
    val traceState: Flow<TraceStateMachine.State>

    fun details(medium: Medium)
    fun trace(channel: ByteArray)
}