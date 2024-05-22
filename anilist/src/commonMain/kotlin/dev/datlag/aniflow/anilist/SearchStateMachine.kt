package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.state.SearchAction
import dev.datlag.aniflow.anilist.state.SearchState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
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
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

@OptIn(ExperimentalCoroutinesApi::class)
class SearchStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<SearchState, SearchAction>(
    initialState = currentState
) {

    var currentState: SearchState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val _type: MutableStateFlow<MediaType> = MutableStateFlow(MediaType.UNKNOWN__)
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

    private val _search: MutableStateFlow<String?> = MutableStateFlow(StateSaver.searchQuery)

    @OptIn(FlowPreview::class)
    private val search = _search.debounce {
        if (it.isNullOrBlank()) {
            0
        } else {
            500
        }
    }

    val searchQuery: String?
        get() = StateSaver.searchQuery
    
    private val query = combine(
        search,
        type,
        nsfw.distinctUntilChanged()
    ) { s, t, n ->
        if (s.isNullOrBlank()) {
            null
        } else {
            PageMediaQuery.Search(
                query = s,
                nsfw = n,
                type = t
            )
        }
    }.distinctUntilChanged()

    init {
        spec {
            inState<SearchState> {
                onEnterEffect {
                    currentState = it
                }
                collectWhileInState(query) { q, state ->
                    state.override {
                        if (q == null) {
                            SearchState.None
                        } else {
                            SearchState.Loading(
                                query = q,
                                fallback = false
                            )
                        }
                    }
                }
            }
            inState<SearchState.Loading> {
                onEnterEffect {
                    StateSaver.searchQuery = it.query.query.ifBlank { null }
                }
                collectWhileInState(
                    flowBuilder = {
                        val usedClient = if (it.fallback) {
                            fallbackClient
                        } else {
                            client
                        }

                        usedClient.query(it.query.toGraphQL()).toFlow()
                    }
                ) { response, state ->
                    state.override {
                        fromGraphQL(response)
                    }
                }
            }
            inState<SearchState.Error> {
                onEnterEffect {
                    crashlytics?.log(it.throwable)
                }
            }
        }
    }

    /**
     * Don't use action as state may not be collected while changing data.
     */
    fun search(query: String) {
        StateSaver.searchQuery = _search.updateAndGet { query.trim() }?.ifBlank { null }
    }

    /**
     * Don't use action as state may not be collected while changing data.
     */
    fun viewAnime() {
        _type.update { MediaType.ANIME }
    }

    /**
     * Don't use action as state may not be collected while changing data.
     */
    fun viewManga() {
        _type.update { MediaType.MANGA }
    }

    /**
     * Don't use action as state may not be collected while changing data.
     */
    fun toggleType() {
        _type.update {
            if (it == MediaType.MANGA) {
                MediaType.ANIME
            } else {
                MediaType.MANGA
            }
        }
    }

    companion object {
        var currentState: SearchState
            get() = StateSaver.searchState
            private set(value) {
                StateSaver.searchState = value
            }
    }
}