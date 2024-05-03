package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.common.nextSeason
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock

class PopularNextSeasonRepository(
    private val apolloClient: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)
    private val type = MutableStateFlow(MediaType.ANIME)
    private val query = combine(page, type, nsfw.distinctUntilChanged()) { p, t, n ->
        val (season, year) = Clock.System.now().nextSeason

        Query(
            page = p,
            type = t,
            nsfw = n,
            season = season,
            year = year
        )
    }
    private val fallbackQuery = query.transform {
        return@transform emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                SeasonState.fromGraphQL(data)
            } else {
                null
            }
        } else {
            SeasonState.fromGraphQL(data)
        }
    }

    val popularNextSeason = query.transform {
        return@transform emitAll(apolloClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                SeasonState.fromGraphQL(data)
            } else {
                null
            }
        } else {
            SeasonState.fromGraphQL(data)
        }
    }.transform {
        return@transform if (it is SeasonState.Error) {
            emitAll(fallbackQuery)
        } else {
            emit(it)
        }
    }

    fun nextPage() = page.getAndUpdate {
        it + 1
    }

    fun previousPage() = page.getAndUpdate {
        it - 1
    }

    fun viewAnime() {
        type.getAndUpdate {
            if (it == MediaType.ANIME) {
                it
            } else {
                page.update { 0 }
                MediaType.ANIME
            }
        }
    }

    fun viewManga() {
        type.getAndUpdate {
            if (it == MediaType.MANGA) {
                it
            } else {
                page.update { 0 }
                MediaType.MANGA
            }
        }
    }

    private data class Query(
        val page: Int,
        val type: MediaType,
        val nsfw: Boolean,
        val season: MediaSeason,
        val year: Int
    ) {
        fun toGraphQL() = SeasonQuery(
            page = Optional.present(page),
            perPage = Optional.present(10),
            adultContent = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(nsfw)
            },
            type = Optional.present(type),
            sort = Optional.present(listOf(MediaSort.POPULARITY_DESC)),
            preventGenres = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(AdultContent.Genre.allTags)
            },
            year = Optional.present(year),
            season = if (season == MediaSeason.UNKNOWN__) {
                Optional.absent()
            } else {
                Optional.present(season)
            },
            statusVersion = Optional.present(2),
            html = Optional.present(true)
        )
    }
}