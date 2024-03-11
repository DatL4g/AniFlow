package dev.datlag.aniflow.ui.navigation.screen.initial.home

import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import kotlinx.coroutines.flow.StateFlow

interface HomeComponent : ContentHolderComponent {
    val trendingState: StateFlow<TrendingAnimeStateMachine.State>
}