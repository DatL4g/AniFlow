package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.model.mapError
import dev.datlag.aniflow.model.saveFirstOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<CharacterStateMachine.State, CharacterStateMachine.Action>(
    initialState = currentState
) {

    init {
        spec {
            inState<State.Waiting> {
                onEnterEffect {
                    currentState = it
                }
                on<Action.Load> { action, state ->
                    state.override { State.Loading(action.id) }
                }
            }
            inState<State.Loading> {
                onEnterEffect {
                    currentState = it
                }
                onEnter { state ->
                    Cache.getCharacter(state.snapshot.query)?.let {
                        return@onEnter state.override { State.Success(state.snapshot.query, it) }
                    }

                    val response = CatchResult.repeat(2, timeoutDuration = 30.seconds) {
                        val query = client.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapError {
                        val query = fallbackClient.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapSuccess<State> {
                        it.Character?.let { data ->
                            State.Success(state.snapshot.query, Character(data))
                        }
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
                onEnterEffect {
                    Cache.setCharacter(it.query, it.character)
                    currentState = it
                }
                on<Action.Load> { action, state ->
                    state.override { State.Loading(action.id) }
                }
            }
            inState<State.Error> {
                onEnterEffect {
                    currentState = it
                }
                on<Action.Load> { action, state ->
                    state.override { State.Loading(action.id) }
                }
            }
        }
    }

    sealed interface State {
        data object Waiting : State
        data class Loading(internal val query: CharacterQuery) : State {
            constructor(id: Int) : this(
                query = CharacterQuery(
                    id = Optional.present(id)
                )
            )
        }
        data class Success(
            internal val query: CharacterQuery,
            val character: Character
        ) : State
        data object Error : State
    }

    sealed interface Action {
        data class Load(val id: Int) : Action
    }

    companion object {
        var currentState: State
            get() = StateSaver.character
            set(value) {
                StateSaver.character = value
            }
    }
}