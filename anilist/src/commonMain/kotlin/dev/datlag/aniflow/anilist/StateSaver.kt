package dev.datlag.aniflow.anilist

internal object StateSaver {
    var trendingAnime: TrendingAnimeStateMachine.State = TrendingAnimeStateMachine.State.Loading(page = 0)
    var airing: AiringTodayStateMachine.State = AiringTodayStateMachine.State.Loading(page = 0)
    var popularSeason: PopularSeasonStateMachine.State = PopularSeasonStateMachine.State.Loading(page = 0)
}