package dev.datlag.aniflow.anilist

import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.state.ListState
import dev.datlag.aniflow.anilist.state.SearchState

internal object StateSaver {
    var airingState: HomeAiringState = HomeAiringState.None

    var trendingState: HomeDefaultState = HomeDefaultState.None
    var popularSeasonState: HomeDefaultState = HomeDefaultState.None
    var popularNextSeasonState: HomeDefaultState = HomeDefaultState.None

    var searchState: SearchState = SearchState.None
    var searchQuery: String? = null

    var listState: ListState = ListState.None
}