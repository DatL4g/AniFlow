package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.type.AiringSort
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class AiringTodayStateMachine(
    private val client: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<AiringTodayStateMachine.State, AiringTodayStateMachine.Action>(
    initialState = State.Loading(0)
) {

    init {
        spec {
            inState<State.Loading> {
                onEnter { state ->
                    val query = AiringQuery(
                        page = Optional.present(state.snapshot.page),
                        perPage = Optional.present(state.snapshot.perPage),
                        sort = Optional.present(listOf(AiringSort.TIME)),
                        airingAtGreater = Optional.present(
                            Clock.System.now().toLocalDateTime(
                                TimeZone.currentSystemDefault()
                            ).date.atStartOfDayIn(
                            TimeZone.currentSystemDefault()
                            ).epochSeconds.toInt()
                        )
                    )

                    Cache.airing.get(query)?.let {
                        return@onEnter state.override { State.Success(query, it) }
                    }

                    val response = CatchResult.result {
                        client.query(query).execute().dataOrThrow()
                    }.mapSuccess<State> {
                        val wantedContent = if (!state.snapshot.adultContent) {
                            val content = it.Page?.airingSchedulesFilterNotNull() ?: emptyList()
                            val filtered = content.filterNot { c ->
                                c.media?.isAdult == true
                            }.filterNot { c ->
                                val genres = c.media?.genresFilterNotNull() ?: emptyList()
                                genres.any { g ->
                                    g.equals("Hentai", ignoreCase = true)
                                            || g.equals("Ecchi", ignoreCase = true)
                                }
                            }
                            filtered
                        } else {
                            it.Page?.airingSchedulesFilterNotNull() ?: emptyList()
                        }

                        State.Success(
                            query,
                            it.copy(
                                Page = it.Page?.copy(
                                    airingSchedules = wantedContent
                                )
                            )
                        )
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
                    Cache.airing.put(it.query, it.data)
                }
            }
            inState<State.Error> {
                on<Action.Retry> { _, state ->
                    state.override {
                        State.Loading(0)
                    }
                }
            }
        }
    }

    sealed interface State {
        data class Loading(
            val page: Int,
            val perPage: Int = 10,
            val adultContent: Boolean = false,
        ) : State

        data class Success(
            internal val query: AiringQuery,
            val data: AiringQuery.Data
        ) : State

        data object Error : State
    }

    sealed interface Action {
        data object Retry : Action
    }
}