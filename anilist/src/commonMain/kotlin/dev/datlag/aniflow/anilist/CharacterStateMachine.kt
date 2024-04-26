package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.model.mapError
import dev.datlag.aniflow.model.safeFirstOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?,
    private val id: Int
) : FlowReduxStateMachine<CharacterStateMachine.State, CharacterStateMachine.Action>(
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
                    val response = CatchResult.repeat(2, timeoutDuration = 30.seconds) {
                        val query = client.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().safeFirstOrNull()?.dataOrThrow()
                    }.mapError {
                        val query = fallbackClient.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().safeFirstOrNull()?.data
                    }.mapSuccess<State> {
                        it.Character?.let { data ->
                            Character(data)?.let { char ->
                                State.Success(state.snapshot.query, char)
                            }
                        }
                    }

                    val cached = Cache.getCharacter(state.snapshot.query)

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
                    Cache.setCharacter(it.query, it.character)
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
        val isLoading: Boolean
            get() = this is Loading

        val isSuccess: Boolean
            get() = this is Success

        data class Loading(internal val query: CharacterQuery) : State {
            constructor(id: Int) : this(
                query = CharacterQuery(
                    id = Optional.present(id),
                    html = Optional.present(true)
                )
            )
        }
        data class Success(
            internal val query: CharacterQuery,
            val character: Character
        ) : State
        data class Error(
            internal val query: CharacterQuery,
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
    }
}