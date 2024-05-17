package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaListStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class MediumRepository(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient
) {

    private val id = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val query = id.filterNotNull().mapLatest { Query(it) }

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
    val medium = query.transformLatest {
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

    fun clear() = id.update { null }

    fun load(id: Int) = this.id.update { id }

    fun updateRatingCall(value: Int): ApolloCall<RatingMutation.Data> {
        val mutation = RatingMutation(
            mediaId = Optional.present(id.value),
            rating = Optional.present(value)
        )

        return client.mutation(mutation)
    }

    fun updateEditCall(progress: Int, status: MediaListStatus, repeat: Int): ApolloCall<EditMutation.Data> {
        val mutation = EditMutation(
            mediaId = Optional.present(id.value),
            progress = if (progress >= 1) {
                Optional.present(progress)
            } else {
                Optional.absent()
            },
            status = if (status != MediaListStatus.UNKNOWN__) {
                Optional.present(status)
            } else {
                Optional.absent()
            },
            repeat = if (repeat >= 1) {
                Optional.present(repeat)
            } else {
                Optional.absent()
            }
        )

        return client.mutation(mutation)
    }

    private data class Query(
        val id: Int,
    ) {
        fun toGraphQL() = MediumQuery(
            id = Optional.present(id),
            statusVersion = Optional.present(2),
            html = Optional.present(true)
        )
    }

    sealed interface State {
        data object None : State
        data class Success(val medium: Medium) : State
        data object Error : State

        companion object {
            fun fromGraphQL(query: MediumQuery.Data?): State {
                val medium = query?.Media?.let(::Medium) ?: return Error

                return Success(medium)
            }
        }
    }
}