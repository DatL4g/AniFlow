package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import kotlinx.coroutines.flow.*

class MediumRepository(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val isLoggedIn: Flow<Boolean>
) {

    private val id = MutableStateFlow<Int?>(null)
    private val query = combine(
        id.filterNotNull(),
        isLoggedIn.distinctUntilChanged()
    ) { t1, _ ->
        Query(t1)
    }
    private val fallbackQuery = query.transform {
        return@transform emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
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

    val medium = query.transform {
        return@transform emitAll(client.query(it.toGraphQL()).toFlow())
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
    }.transform {
        return@transform if (it is State.Error) {
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