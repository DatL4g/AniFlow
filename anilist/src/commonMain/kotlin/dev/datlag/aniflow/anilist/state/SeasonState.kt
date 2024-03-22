package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.SeasonQuery
import dev.datlag.aniflow.anilist.common.season
import dev.datlag.aniflow.anilist.common.year
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

sealed interface SeasonState {
    data class Loading(
        internal val query: SeasonQuery
    ) : SeasonState {
        constructor(
            page: Int,
            perPage: Int = 10,
            adultContent: Boolean = false,
            type: MediaType = MediaType.ANIME,
            season: MediaSeason = Clock.System.now().season,
            year: Int = Clock.System.now().year
        ) : this(
            SeasonQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                adultContent = if (!adultContent) {
                    Optional.present(adultContent)
                } else {
                    Optional.absent()
                },
                type = Optional.present(type),
                sort = Optional.present(listOf(MediaSort.POPULARITY_DESC)),
                preventGenres = if (!adultContent) {
                    Optional.present(AdultContent.Genre.allTags)
                } else {
                    Optional.absent()
                },
                year = Optional.present(year),
                season = if (season == MediaSeason.UNKNOWN__) {
                    Optional.absent()
                } else {
                    Optional.present(season)
                }
            )
        )

        constructor(
            page: Int,
            perPage: Int = 10,
            adultContent: Boolean = false,
            type: MediaType = MediaType.ANIME,
            now: Instant = Clock.System.now()
        ) : this(
            page = page,
            perPage = perPage,
            adultContent = adultContent,
            type = type,
            season = now.season,
            year = now.year
        )
    }

    data class Success(
        internal val query: SeasonQuery,
        val data: SeasonQuery.Data
    ) : SeasonState

    data class Error(
        internal val query: SeasonQuery
    ) : SeasonState
}

sealed interface SeasonAction {
    data object Retry : SeasonAction
}