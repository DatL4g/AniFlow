package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

class TrendingRepository(
    private val apolloClient: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)
    private val type = viewManga.distinctUntilChanged().map {
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
    private val fallbackQuery = query.transform {
        return@transform emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                CollectionState.fromTrendingGraphQL(data)
            } else {
                null
            }
        } else {
            CollectionState.fromTrendingGraphQL(data)
        }
    }

    val trending = query.transform {
        return@transform emitAll(apolloClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                CollectionState.fromTrendingGraphQL(data)
            } else {
                null
            }
        } else {
            CollectionState.fromTrendingGraphQL(data)
        }
    }.transform {
        return@transform if (it.isError) {
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
        fun toGraphQL() = TrendingQuery(
            page = Optional.present(page),
            perPage = Optional.present(20),
            adultContent = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(nsfw)
            },
            type = Optional.present(type),
            sort = Optional.present(listOf(MediaSort.TRENDING_DESC)),
            preventGenres = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(AdultContent.Genre.allTags)
            },
            statusVersion = Optional.present(2),
            html = Optional.present(true)
        )
    }
}