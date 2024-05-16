package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class ListRepository(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val user: Flow<User?>,
    private val viewManga: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)
    private val _type = MutableStateFlow(MediaType.UNKNOWN__)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val type = _type.transformLatest {
        return@transformLatest if (it == MediaType.UNKNOWN__) {
            emitAll(viewManga.map { m ->
                if (m) {
                    MediaType.MANGA
                } else {
                    MediaType.ANIME
                }
            })
        } else {
            emit(it)
        }
    }.distinctUntilChanged()

    private val query = combine(
        page,
        type,
        user.filterNotNull().distinctUntilChanged(),
    ) { p, t, u ->
        Query(
            page = p,
            type = t,
            userId = u.id
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val fallbackQuery = query.transformLatest {
        return@transformLatest emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                State.fromGraphQL(data)
            } else {
                null
            }
        } else {
            State.fromGraphQL(data)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val list = query.transformLatest {
        return@transformLatest emitAll(client.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                State.fromGraphQL(data)
            } else {
                null
            }
        } else {
            State.fromGraphQL(data)
        }
    }.transformLatest {
        return@transformLatest if (it is State.Error) {
            emitAll(fallbackQuery)
        } else {
            emit(it)
        }
    }

    private data class Query(
        val page: Int,
        val type: MediaType,
        val userId: Int
    ) {
        fun toGraphQL() = ListQuery(
            page = Optional.present(page),
            type = if (type == MediaType.UNKNOWN__) {
                Optional.absent()
            } else {
                Optional.present(type)
            },
            userId = Optional.present(userId)
        )
    }

    sealed interface State {
        data object None : State

        data class Success(
            val hasNextPage: Boolean,
            val medium: Collection<Medium>
        ) : State

        data object Error : State

        companion object {
            fun fromGraphQL(query: ListQuery.Data?): State {
                val medium = query?.Page?.mediaListFilterNotNull()?.mapNotNull {
                    Medium(
                        media = it.media ?: return@mapNotNull null,
                        list = it
                    )
                } ?: return Error

                return Success(
                    hasNextPage = query.Page.pageInfo?.hasNextPage ?: false,
                    medium = medium.distinctBy { it.id }
                )
            }
        }
    }
}