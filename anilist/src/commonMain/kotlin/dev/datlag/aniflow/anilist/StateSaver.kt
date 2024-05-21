package dev.datlag.aniflow.anilist

import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.anilist.state.HomeDefaultState

internal object StateSaver {
    var airingState: HomeAiringState = HomeAiringState.None

    var trendingState: HomeDefaultState = HomeDefaultState.None
    var popularSeasonState: HomeDefaultState = HomeDefaultState.None
    var popularNextSeasonState: HomeDefaultState = HomeDefaultState.None
}