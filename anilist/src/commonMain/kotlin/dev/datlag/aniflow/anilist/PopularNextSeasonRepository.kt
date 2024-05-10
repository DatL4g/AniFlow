package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.common.nextSeason
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock

class PopularNextSeasonRepository(
    private val apolloClient: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)

    private val query = combine(page, nsfw.distinctUntilChanged()) { p, n ->
        val (season, year) = Clock.System.now().nextSeason

        Query(
            page = p,
            nsfw = n,
            season = season,
            year = year
        )
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val fallbackQuery = query.transformLatest {
        return@transformLatest emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                CollectionState.fromSeasonGraphQL(data)
            } else {
                null
            }
        } else {
            CollectionState.fromSeasonGraphQL(data)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val popularNextSeason = query.transformLatest {
        return@transformLatest emitAll(apolloClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                CollectionState.fromSeasonGraphQL(data)
            } else {
                null
            }
        } else {
            CollectionState.fromSeasonGraphQL(data)
        }
    }.transformLatest {
        return@transformLatest if (it.isError) {
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

    private data class Query(
        val page: Int,
        val nsfw: Boolean,
        val season: MediaSeason,
        val year: Int
    ) {
        fun toGraphQL() = SeasonQuery(
            page = Optional.present(page),
            perPage = Optional.present(20),
            adultContent = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(nsfw)
            },
            type = Optional.present(MediaType.ANIME),
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