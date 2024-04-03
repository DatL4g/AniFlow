package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.state.SeasonAction
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PopularNextSeasonStateMachine(
    private val client: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<SeasonState, SeasonAction>(
    initialState = currentState
) {

    init {
        spec {
            inState<SeasonState.Loading> {
                onEnterEffect {
                    currentState = it
                }
                onEnter { state ->
                    Cache.getSeason(state.snapshot.query)?.let {
                        return@onEnter state.override { SeasonState.Success(query, it) }
                    }

                    val response = CatchResult.result {
                        client.query(state.snapshot.query).execute().dataOrThrow()
                    }.mapSuccess<SeasonState> {
                        SeasonState.Success(state.snapshot.query, it)
                    }

                    state.override {
                        response.asSuccess {
                            crashlytics?.log(it)

                            if (state.snapshot.retry <= 3) {
                                SeasonState.Loading(state.snapshot.query, state.snapshot.retry + 1)
                            } else {
                                SeasonState.Error(query)
                            }
                        }
                    }
                }
            }
            inState<SeasonState.Success> {
                onEnterEffect {
                    Cache.setSeason(it.query, it.data)
                    currentState = it
                }
            }
            inState<SeasonState.Error> {
                onEnterEffect {
                    currentState = it
                }
                on<SeasonAction.Retry> { _, state ->
                    state.override {
                        SeasonState.Loading(
                            state.snapshot.query
                        )
                    }
                }
            }
        }
    }

    companion object {
        var currentState: SeasonState
            get() = StateSaver.popularNextSeason
            set(value) {
                StateSaver.popularNextSeason = value
            }
    }
}