package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.state.HomeDefaultAction
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class PopularSeasonStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean>,
    private val viewManga: Flow<Boolean>,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<HomeDefaultState, HomeDefaultAction>(
    initialState = currentState
) {

    var currentState: HomeDefaultState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val type = viewManga.mapLatest {
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
        }
    }.distinctUntilChanged()

    private val query = combine(
        type,
        nsfw.distinctUntilChanged()
    ) { t, n ->
        PageMediaQuery.PopularSeason(
            type = t,
            nsfw = n
        )
    }.distinctUntilChanged()

    init {
        spec {
            inState<HomeDefaultState> {
                onEnterEffect {
                    currentState = it
                }
                collectWhileInState(query) { q, state ->
                    state.override {
                        HomeDefaultState.Loading(
                            query = q,
                            fallback = false
                        )
                    }
                }
            }
            inState<HomeDefaultState.Loading> {
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
            inState<HomeDefaultState.Error> {
                onEnterEffect {
                    crashlytics?.log(it.throwable)
                }
            }
        }
    }

    companion object {
        var currentState: HomeDefaultState
            get() = StateSaver.popularSeasonState
            private set(value) {
                StateSaver.popularSeasonState = value
            }
    }
}