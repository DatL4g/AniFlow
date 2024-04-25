package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.model.mapError
import dev.datlag.aniflow.model.saveFirstOrNull
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MediumStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?,
    private val id: Int,
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
                    val response = CatchResult.repeat(times = 2, timeoutDuration = 30.seconds) {
                        val query = client.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapError {
                        val query = fallbackClient.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.data
                    }.mapSuccess<State> {
                        it.Media?.let { data ->
                            State.Success(state.snapshot.query, Medium(data))
                        }
                    }

                    val cached = Cache.getMedium(state.snapshot.query)

                    state.override {
                        response.asSuccess {
                            crashlytics?.log(it)

                            if (cached != null) {
                                State.Success(query, cached)
                            } else {
                                State.Error(query)
                            }
                        }
                    }
                }
            }
            inState<State.Success> {
                onEnterEffect {
                    Cache.setMedium(it.query, it.data)
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
                    html = Optional.present(true)
                )
            )
        }

        data class Success(
            internal val query: MediumQuery,
            val data: Medium
        ) : State

        data class Error(
            internal val query: MediumQuery,
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
    }
}