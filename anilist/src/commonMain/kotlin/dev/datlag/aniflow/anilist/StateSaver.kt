package dev.datlag.aniflow.anilist

internal object StateSaver {
    var trendingAnime: TrendingAnimeStateMachine.State = TrendingAnimeStateMachine.State.Loading(page = 0)
}