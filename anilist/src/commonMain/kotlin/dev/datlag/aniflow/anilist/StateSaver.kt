package dev.datlag.aniflow.anilist

import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.state.ListState
import dev.datlag.aniflow.anilist.state.SearchState

internal object StateSaver {
    var airingState: HomeAiringState = HomeAiringState.Loading

    var trendingState: HomeDefaultState = HomeDefaultState.Loading
    var popularSeasonState: HomeDefaultState = HomeDefaultState.Loading
    var popularNextSeasonState: HomeDefaultState = HomeDefaultState.Loading

    var searchState: SearchState = SearchState.None
    var searchQuery: String? = null

    var listState: ListState = ListState.Loading(emptyList())

    var discoverState: DiscoverState = DiscoverState.Recommended.Loading.WatchList
}