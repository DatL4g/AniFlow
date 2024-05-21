package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.PageAiringQuery
import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.anilist.state.HomeDefaultAction
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class AiringTodayStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<HomeAiringState, HomeDefaultAction>(
    initialState = currentState
) {

    var currentState: HomeAiringState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val query = nsfw.distinctUntilChanged().mapLatest {
        PageAiringQuery.Today(
            nsfw = it
        )
    }.distinctUntilChanged()

    init {
        spec {
            inState<HomeAiringState> {
                onEnterEffect {
                    currentState = it
                }
                collectWhileInState(query) { q, state ->
                    state.override {
                        HomeAiringState.Loading(
                            query = q,
                            fallback = false
                        )
                    }
                }
            }
            inState<HomeAiringState.Loading> {
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
            inState<HomeAiringState.Error> {
                onEnterEffect {
                    crashlytics?.log(it.throwable)
                }
            }
        }
    }

    companion object {
        var currentState: HomeAiringState
            get() = StateSaver.airingState
            private set(value) {
                StateSaver.airingState = value
            }
    }
}