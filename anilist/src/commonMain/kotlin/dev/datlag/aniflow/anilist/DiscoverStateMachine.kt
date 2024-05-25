package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.common.season
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.state.DiscoverAction
import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val user: Flow<User?>,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?,
) : FlowReduxStateMachine<DiscoverState, DiscoverAction>(
    initialState = currentState
) {

    var currentState: DiscoverState
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

    private val recommendationQuery = combine(
        type,
        user.mapNotNull { it?.id }.distinctUntilChanged()
    ) { t, u ->
        PageListQuery.Recommendation(
            type = t,
            userId = u
        )
    }.distinctUntilChanged()

    private val _season: MutableStateFlow<MediaSeason> = MutableStateFlow(MediaSeason.UNKNOWN__)
    val season = _season.transformLatest {
        return@transformLatest if (it == MediaSeason.UNKNOWN__) {
            emit(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month.season)
        } else {
            emit(it)
        }
    }.distinctUntilChanged()

    private val seasonQuery = combine(
        type,
        season,
        nsfw.distinctUntilChanged(),
    ) { t, s, n ->
        PageMediaQuery.Season(
            type = t,
            season = s,
            nsfw = n
        )
    }.distinctUntilChanged()

    init {
        spec {
            inState<DiscoverState> {
                onEnterEffect {
                    currentState = it
                }
                onActionEffect<DiscoverAction.Type> { action, _ ->
                    when (action) {
                        is DiscoverAction.Type.Anime -> _type.update { MediaType.ANIME }
                        is DiscoverAction.Type.Manga -> _type.update { MediaType.MANGA }
                        is DiscoverAction.Type.Toggle -> _type.update {
                            if (it == MediaType.MANGA) {
                                MediaType.ANIME
                            } else {
                                MediaType.MANGA
                            }
                        }
                    }
                }
                on<DiscoverAction.ListType.Recommendation> { _, state ->
                    if (state is DiscoverState.Recommended) {
                        state.noChange()
                    } else {
                        state.override {
                            DiscoverState.Recommended.None
                        }
                    }
                }
            }

            inState<DiscoverState.Recommended> {
                on<DiscoverAction.ListType.Season> { action, state ->
                    state.override {
                        DiscoverState.Season.None(
                            wanted = action.mediaSeason
                        )
                    }
                }
                collectWhileInState(recommendationQuery) { q, state ->
                    state.override {
                        DiscoverState.Recommended.Loading.WatchList(
                            query = q,
                            fallback = false
                        )
                    }
                }
            }
            inState<DiscoverState.Recommended.Loading.WatchList> {
                collectWhileInState(
                    flowBuilder = {
                        val usedClient = if (it.fallback) {
                            fallbackClient
                        } else {
                            client
                        }

                        combine(
                            nsfw.distinctUntilChanged(),
                            usedClient.query(it.query.toGraphQL()).toFlow()
                        ) { n, r ->
                            n to r
                        }
                    }
                ) { (adult, response), state ->
                    state.override {
                        fromGraphQL(adult, response)
                    }
                }
            }
            inState<DiscoverState.Recommended.Loading.Matching> {
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

            inState<DiscoverState.Season.Loading> {
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

            inState<DiscoverState.Error> {
                onEnterEffect {
                    crashlytics?.log(it.throwable)
                }
            }
        }
    }

    companion object {
        var currentState: DiscoverState
            get() = StateSaver.discoverState
            private set(value) {
                StateSaver.discoverState = value
            }
    }
}