package dev.datlag.aniflow.anilist

import dev.datlag.aniflow.anilist.common.nextSeason
import dev.datlag.aniflow.anilist.state.SeasonState
import kotlinx.datetime.Clock

internal object StateSaver {
    var popularSeason: SeasonState = SeasonState.Loading(page = 0)
    var popularNextSeason: SeasonState = run {
        val (nextSeason, nextYear) = Clock.System.now().nextSeason

        SeasonState.Loading(
            page = 0,
            season = nextSeason,
            year = nextYear
        )
    }
}