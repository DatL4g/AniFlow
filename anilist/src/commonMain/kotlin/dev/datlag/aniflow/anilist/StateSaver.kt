package dev.datlag.aniflow.anilist

import dev.datlag.aniflow.anilist.state.HomeDefaultState

internal object StateSaver {
    var trendingState: HomeDefaultState = HomeDefaultState.None
    var popularSeasonState: HomeDefaultState = HomeDefaultState.None
    var popularNextSeasonState: HomeDefaultState = HomeDefaultState.None
}