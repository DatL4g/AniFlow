package dev.datlag.aniflow.ui.navigation.screen.initial.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.AiringTodayRepository
import dev.datlag.aniflow.anilist.TrendingRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.trace.TraceStateMachine
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle

interface HomeComponent : ContentHolderComponent {
    val titleLanguage: Flow<SettingsTitle?>

    val airingState: Flow<AiringTodayRepository.State>
    val trendingState: Flow<TrendingRepository.State>
    val popularSeasonState: Flow<SeasonState>
    val popularNextSeasonState: Flow<SeasonState>

    val traceState: Flow<TraceStateMachine.State>

    fun details(medium: Medium)
    fun trace(channel: ByteArray)
}