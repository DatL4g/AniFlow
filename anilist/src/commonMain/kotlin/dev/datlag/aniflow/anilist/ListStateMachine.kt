package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.state.ListAction
import dev.datlag.aniflow.anilist.state.ListState
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class ListStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val user: Flow<User?>,
    private val viewManga: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?,
) : FlowReduxStateMachine<ListState, ListAction>(
    initialState = currentState
) {

    var currentState: ListState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val page = MutableStateFlow(0)
    private val _type = MutableStateFlow(MediaType.UNKNOWN__)
    private val _status = MutableStateFlow(MediaListStatus.UNKNOWN__)
    val status: StateFlow<MediaListStatus> = _status

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

    private val query = combine(
        page,
        type,
        _status,
        user.mapNotNull { it?.id }.distinctUntilChanged()
    ) { p, t, s, u ->
        PageListQuery.ForPage(
            page = p,
            type = t,
            status = s,
            userId = u
        )
    }.distinctUntilChanged()

    init {
        spec {
            inState<ListState> {
                onEnterEffect {
                    currentState = it
                }
                onActionEffect<ListAction.Page> { action, _ ->
                    when (action) {
                        is ListAction.Page.Next -> {
                            page.update { it + 1 }
                        }
                    }
                }
                onActionEffect<ListAction.Type> { action, _ ->
                    when (action) {
                        is ListAction.Type.Anime -> {
                            page.update { 0 }
                            _type.update {
                                MediaType.ANIME
                            }
                        }
                        is ListAction.Type.Manga -> {
                            page.update { 0 }
                            _type.update {
                                MediaType.MANGA
                            }
                        }
                        is ListAction.Type.Toggle -> {
                            page.update { 0 }
                            _type.update {
                                if (it == MediaType.MANGA) {
                                    MediaType.ANIME
                                } else {
                                    MediaType.MANGA
                                }
                            }
                        }
                    }
                }
                onActionEffect<ListAction.Status> { action, _ ->
                    page.update { 0 }
                    _status.update {
                        action.value
                    }
                }
                collectWhileInState(query) { q, state ->
                    state.override {
                        val collection = if (q.page == 0) {
                            emptyList()
                        } else {
                            (this as? ListState.Data)?.collection.orEmpty()
                        }

                        ListState.Loading(
                            query = q,
                            fallback = false,
                            collection = collection
                        )
                    }
                }
            }
            inState<ListState.Loading> {
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
            inState<ListState.Error> {
                onEnterEffect {
                    crashlytics?.log(it.throwable)
                }
            }
        }
    }

    companion object {
        var currentState
            get() = StateSaver.listState
            private set(value) {
                StateSaver.listState = value
            }
    }
}