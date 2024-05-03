package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Character
import kotlinx.coroutines.flow.*

class CharacterRepository(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient
) {

    private val id = MutableStateFlow<Int?>(null)
    private val query = id.filterNotNull().map {
        Query(it)
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

    val character = query.transform {
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
        return@transform if (it is Error) {
            emitAll(fallbackQuery)
        } else {
            emit(it)
        }
    }

    fun clear() = id.update { null }

    fun load(id: Int) = this.id.update { id }

    private data class Query(
        val id: Int
    ) {
        fun toGraphQL() = CharacterQuery(
            id = Optional.present(id),
            html = Optional.present(true)
        )
    }

    sealed interface State {
        data class Success(val character: Character) : State
        data object Error : State

        companion object {
            fun fromGraphQL(query: CharacterQuery.Data?): State {
                val char = query?.Character?.let { Character(it) }

                if (char == null) {
                    return Error
                }

                return Success(char)
            }
        }
    }
}