package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.map
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.model.CatchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class, ApolloExperimental::class)
class TrendingAnimeStateMachine(
    private val client: ApolloClient
) : FlowReduxStateMachine<TrendingAnimeStateMachine.State, TrendingAnimeStateMachine.Action>(
    initialState = currentState
) {

    init {
        spec {
            inState<State.Loading> {
                onEnterEffect {
                    currentState = it
                }
                onEnter { state ->
                    Cache.trendingAnime.get(state.snapshot.query)?.let {
                        return@onEnter state.override { State.Success(query, it) }
                    }

                    val response = CatchResult.result {
                        client.query(state.snapshot.query).execute().dataOrThrow()
                    }.mapSuccess<State> {
                        val data = it.copy(
                            Page = it.Page?.copy(
                                media = listOfNotNull(
                                    *(state.snapshot.data?.Page?.mediaFilterNotNull()?.toTypedArray() ?: emptyArray()),
                                    *(it.Page.mediaFilterNotNull()?.toTypedArray() ?: emptyArray())
                                )
                            )
                        )
                        State.Success(state.snapshot.query, data)
                    }

                    state.override {
                        response.asSuccess {
                            State.Error(query, state.snapshot.data)
                        }
                    }
                }
            }
            inState<State.Success> {
                onEnterEffect {
                    Cache.trendingAnime.put(it.query, it.data)
                    currentState = it
                }
                on<Action.Next> { _, state ->
                    state.override {
                        State.Loading(
                            query.copy(
                                page = query.page.map { it?.plus(1) }
                            ),
                            state.snapshot.data
                        )
                    }
                }
            }
            inState<State.Error> {
                onEnterEffect {
                    currentState = it
                }
                on<Action.Retry> { _, state ->
                    state.override {
                        State.Loading(
                            state.snapshot.query,
                            state.snapshot.data
                        )
                    }
                }
            }
        }
    }

    sealed interface State {
        data class Loading(
            internal val query: HomeQuery,
            val data: HomeQuery.Data?
        ) : State {
            constructor(
                page: Int,
                perPage: Int = 10,
                adultContent: Boolean = false,
                type: MediaType = MediaType.ANIME,
                data: HomeQuery.Data? = null
            ) : this(
                HomeQuery(
                    page = Optional.present(page),
                    perPage = Optional.present(perPage),
                    adultContent = Optional.present(adultContent),
                    type = Optional.present(type),
                    sort = Optional.present(listOf(MediaSort.TRENDING_DESC))
                ),
                data
            )
        }

        data class Success(
            internal val query: HomeQuery,
            val data: HomeQuery.Data
        ) : State

        data class Error(
            internal val query: HomeQuery,
            val data: HomeQuery.Data?
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
        data object Next : Action
    }

    companion object {
        var currentState: TrendingAnimeStateMachine.State
            get() = StateSaver.trendingAnime
            set(value) {
                StateSaver.trendingAnime = value
            }
    }
}