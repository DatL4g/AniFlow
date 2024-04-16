package dev.datlag.aniflow.trace

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.trace.model.SearchResponse
import io.ktor.client.request.forms.*
import io.ktor.utils.io.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class TraceStateMachine(
    private val trace: Trace,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<TraceStateMachine.State, TraceStateMachine.Action>(
    initialState = State.Waiting
) {

    init {
        spec {
            inState<State.Waiting> {
                on<Action.Load> { action, state ->
                    state.override { State.Loading(action.image) }
                }
            }
            inState<State.Loading> {
                onEnter { state ->
                    val response = CatchResult.repeat(2) {
                        val result = trace.search(state.snapshot.image)

                        if (result.isError) {
                            throw IllegalStateException("Result is Error")
                        } else {
                            result
                        }
                    }.mapSuccess<State> {
                        State.Success(it)
                    }

                    state.override {
                        response.asSuccess {
                            crashlytics?.log(it)

                            State.Error
                        }
                    }
                }
            }
            inState<State.Success> {
                on<Action.Load> { action, state ->
                    state.override { State.Loading(action.image) }
                }
            }
            inState<State.Error> {
                on<Action.Load> { action, state ->
                    state.override { State.Loading(action.image) }
                }
            }
        }
    }

    sealed interface State {

        val isWaiting: Boolean
            get() = this is Waiting

        val isLoading: Boolean
            get() = this is Loading

        data object Waiting : State

        class Loading(
            internal val image: ByteArray
        ) : State

        data class Success(
            val response: SearchResponse
        ) : State

        data object Error : State
    }

    sealed interface Action {
        class Load(internal val image: ByteArray) : Action
    }
}