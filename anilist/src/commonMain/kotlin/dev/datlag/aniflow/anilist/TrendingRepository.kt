package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.flow.*

class TrendingRepository(
    private val apolloClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)
    private val type = MutableStateFlow(MediaType.ANIME)
    private val query = combine(page, type, nsfw.distinctUntilChanged()) { p, t, n ->
        Query(
            page = p,
            type = t,
            nsfw = n
        )
    }

    val trending = query.transform {
        return@transform emitAll(apolloClient.query(it.toGraphQL()).toFlow())
    }.map {
        State.fromGraphQL(it.data)
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

    sealed interface State {
        data class Success(
            val collection: Collection<Medium>
        ) : State

        data object Error : State

        companion object {
            fun fromGraphQL(data: TrendingQuery.Data?): State {
                val mediaList = data?.Page?.mediaFilterNotNull()

                if (mediaList.isNullOrEmpty()) {
                    return Error
                }

                return Success(mediaList.map { Medium(it) })
            }
        }
    }
}