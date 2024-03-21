package dev.datlag.aniflow.ui.navigation.screen.initial.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import kotlinx.coroutines.flow.StateFlow

interface HomeComponent : ContentHolderComponent {
    val airingState: StateFlow<AiringTodayStateMachine.State>
    val trendingState: StateFlow<TrendingAnimeStateMachine.State>
    val popularSeasonState: StateFlow<PopularSeasonStateMachine.State>

    val child: Value<ChildSlot<HomeConfig, Component>>

    fun details(medium: Medium)
}