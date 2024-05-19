package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update

class SearchRepository(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false),
) {

    private val search = MutableStateFlow<String?>(null)
    private val _type = MutableStateFlow(MediaType.UNKNOWN__)

    val searchQuery: String?
        get() = search.value?.ifBlank { null }

    @OptIn(FlowPreview::class)
    private val searchDebounced = search.debounce {
        if (it.isNullOrBlank()) {
            0
        } else {
            100
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val type = _type.transformLatest {
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

    private val query = combine(searchDebounced, type, nsfw.distinctUntilChanged()) { s, t, n ->
        if (s.isNullOrBlank()) {
            null
        } else {
            Query(
                search = s,
                nsfw = n,
                type = t
            )
        }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val fallbackQuery = query.transformLatest {
        return@transformLatest if (it == null) {
            emit(it)
        } else {
            emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
        }
    }.mapLatest {
        if (it == null) {
            return@mapLatest CollectionState.None
        }

        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                CollectionState.fromSearchGraphQL(data)
            } else {
                CollectionState.None
            }
        } else {
            CollectionState.fromSearchGraphQL(data)
        }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val result = query.transformLatest {
        return@transformLatest if (it == null) {
            emit(it)
        } else {
            emitAll(client.query(it.toGraphQL()).toFlow())
        }
    }.mapLatest {
        if (it == null) {
            return@mapLatest CollectionState.None
        }

        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                CollectionState.fromSearchGraphQL(data)
            } else {
                CollectionState.None
            }
        } else {
            CollectionState.fromSearchGraphQL(data)
        }
    }.transformLatest {
        return@transformLatest if (it.isError) {
            emitAll(fallbackQuery)
        } else {
            emit(it)
        }
    }

    fun query(query: String) {
        search.update { query }
    }

    fun setType(type: MediaType) {
        _type.update { type }
    }

    fun viewAnime() = setType(MediaType.ANIME)
    fun viewManga() = setType(MediaType.MANGA)

    fun toggleType() {
        _type.update {
            if (it == MediaType.MANGA) {
                MediaType.ANIME
            } else {
                MediaType.MANGA
            }
        }
    }

    private data class Query(
        val search: String,
        val nsfw: Boolean,
        val type: MediaType
    ) {
        fun toGraphQL() = SearchQuery(
            query = Optional.present(search),
            adultContent = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(nsfw)
            },
            preventGenres = if (nsfw) {
                Optional.absent()
            } else {
                Optional.present(AdultContent.Genre.allTags)
            },
            type = if (type == MediaType.UNKNOWN__) {
                Optional.absent()
            } else {
                Optional.present(type)
            }
        )
    }
}