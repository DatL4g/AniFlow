package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.map
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.*
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class, ApolloExperimental::class)
class TrendingAnimeStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?
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
                    Cache.getTrending(state.snapshot.query)?.let {
                        return@onEnter state.override { State.Success(query, it) }
                    }

                    val response = CatchResult.repeat(2) {
                        val query = client.query(state.snapshot.query)

                         query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapError {
                        val query = fallbackClient.query(state.snapshot.query)
                        
                        query.execute().data ?: query.toFlow().saveFirstOrNull()?.dataOrThrow()
                    }.mapSuccess<State> {
                        State.Success(state.snapshot.query, it)
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
                    Cache.setTrending(it.query, it.data)
                    currentState = it
                }
            }
            inState<State.Error> {
                onEnterEffect {
                    currentState = it
                }
                on<Action.Retry> { _, state ->
                    state.override {
                        State.Loading(
                            state.snapshot.query
                        )
                    }
                }
            }
        }
    }

    sealed interface State {
        data class Loading(
            internal val query: TrendingQuery
        ) : State {
            constructor(
                page: Int,
                perPage: Int = 10,
                adultContent: Boolean = false,
                type: MediaType = MediaType.ANIME
            ) : this(
                TrendingQuery(
                    page = Optional.present(page),
                    perPage = Optional.present(perPage),
                    adultContent = if (!adultContent) {
                        Optional.present(adultContent)
                    } else {
                        Optional.absent()
                    },
                    type = Optional.present(type),
                    sort = Optional.present(listOf(MediaSort.TRENDING_DESC)),
                    preventGenres = if (!adultContent) {
                        Optional.present(AdultContent.Genre.allTags)
                    } else {
                        Optional.absent()
                    }
                )
            )
        }

        data class Success(
            internal val query: TrendingQuery,
            val data: TrendingQuery.Data
        ) : State

        data class Error(
            internal val query: TrendingQuery
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
    }

    companion object {
        var currentState: TrendingAnimeStateMachine.State
            get() = StateSaver.trendingAnime
            set(value) {
                StateSaver.trendingAnime = value
            }
    }
}