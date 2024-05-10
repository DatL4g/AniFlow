package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class PopularSeasonRepository(
    private val apolloClient: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val type = viewManga.distinctUntilChanged().mapLatest {
        page.update { 0 }
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
        }
    }

    private val query = combine(page, type, nsfw.distinctUntilChanged()) { p, t, n ->
        Query(
            page = p,
            type = t,
            nsfw = n
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
    val popularThisSeason = query.transformLatest {
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
        val type: MediaType,
        val nsfw: Boolean
    ) {
        fun toGraphQL() = SeasonQuery(
            page = Optional.present(page),
            perPage = Optional.present(20),
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
            year = Optional.absent(),
            season = Optional.absent(),
            statusVersion = Optional.present(2),
            html = Optional.present(true)
        )
    }
}