package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.AiringSort
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.model.mapError
import dev.datlag.aniflow.model.saveFirstOrNull
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.safeSubList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
class AiringTodayStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<AiringTodayStateMachine.State, AiringTodayStateMachine.Action>(
    initialState = currentState
) {

    init {
        spec {
            inState<State.Loading> {
                onEnterEffect {
                    currentState = it
                }
                onEnter { state ->
                    Cache.getAiring(state.snapshot.query)?.let {
                        return@onEnter state.override { State.Success(query, it) }
                    }

                    val response = CatchResult.repeat(times = 2) {
                        val query = client.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapError {
                        val query = fallbackClient.query(state.snapshot.query)

                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapSuccess<State> {
                        val wantedContent = if (!state.snapshot.adultContent) {
                            val content = it.Page?.airingSchedulesFilterNotNull() ?: emptyList()
                            val filtered = content.filterNot { c ->
                                c.media?.isAdult == true
                            }.filterNot { c ->
                                val genres = c.media?.genresFilterNotNull() ?: emptyList()

                                genres.any { g ->
                                    AdultContent.Genre.exists(g)
                                }
                            }
                            filtered
                        } else {
                            it.Page?.airingSchedulesFilterNotNull() ?: emptyList()
                        }

                        State.Success(
                            state.snapshot.query,
                            it.copy(
                                Page = it.Page?.copy(
                                    airingSchedules = wantedContent.safeSubList(0, 10)
                                )
                            )
                        )
                    }

                    state.override {
                        response.asSuccess {
                            crashlytics?.log(it)

                            State.Error(query, adultContent)
                        }
                    }
                }
            }
            inState<State.Success> {
                onEnterEffect {
                    Cache.setAiring(it.query, it.data)

                    currentState = it
                }
            }
            inState<State.Error> {
                onEnterEffect {
                    currentState = it
                }
                on<Action.Retry> { _, state ->
                    state.override {
                        State.Loading(state.snapshot.query, state.snapshot.adultContent)
                    }
                }
            }
        }
    }

    sealed interface State {
        data class Loading(
            internal val query: AiringQuery,
            val adultContent: Boolean = false
        ) : State {
            constructor(
                page: Int,
                perPage: Int = 20,
                adultContent: Boolean = false
            ) : this(
                query = AiringQuery(
                    page = Optional.present(page),
                    perPage = Optional.present(perPage),
                    sort = Optional.present(listOf(AiringSort.TIME)),
                    airingAtGreater = Optional.present(
                        Clock.System.now().minus(1.hours).epochSeconds.toInt()
                    )
                ),
                adultContent = adultContent
            )
        }

        data class Success(
            internal val query: AiringQuery,
            val data: AiringQuery.Data
        ) : State

        data class Error(
            internal val query: AiringQuery,
            val adultContent: Boolean = false
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
    }

    companion object {
        var currentState: State
            get() = StateSaver.airing
            set(value) {
                StateSaver.airing = value
            }
    }
}