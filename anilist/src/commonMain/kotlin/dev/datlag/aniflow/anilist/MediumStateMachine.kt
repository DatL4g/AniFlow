package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MediumStateMachine(
    private val client: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?,
    private val id: Int
) : FlowReduxStateMachine<MediumStateMachine.State, MediumStateMachine.Action>(
    initialState = State.Loading(id)
) {

    var currentState: State = State.Loading(id)
        private set

    init {
        spec {
            inState<State.Loading> {
                onEnterEffect {
                    currentState = it
                }
                onEnter { state ->
                    Cache.medium.get(state.snapshot.query)?.let {
                        return@onEnter state.override { State.Success(query, it) }
                    }

                    val response = CatchResult.result {
                        client.query(state.snapshot.query).execute().dataOrThrow()
                    }.mapSuccess<State> {
                        it.Media?.let { data ->
                            State.Success(state.snapshot.query, Medium.Full(data))
                        }
                    }

                    state.override {
                        response.asSuccess {
                            crashlytics?.log(it)

                            State.Error(query)
                        }
                    }
                }
            }
            inState<State.Success> {
                onEnterEffect {
                    Cache.medium.put(it.query, it.data)
                    currentState = it
                }
            }
            inState<State.Error> {
                onEnterEffect {
                    currentState = it
                }
                on<Action.Retry> { _, state ->
                    state.override {
                        State.Loading(state.snapshot.query)
                    }
                }
            }
        }
    }

    sealed interface State {
        data class Loading(
            internal val query: MediumQuery
        ) : State {
            constructor(id: Int) : this(
                MediumQuery(
                    id = Optional.present(id),
                    statusVersion = Optional.present(2),
                    html = Optional.present(false)
                )
            )
        }

        data class Success(
            internal val query: MediumQuery,
            val data: Medium.Full
        ) : State

        data class Error(
            internal val query: MediumQuery,
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
    }
}