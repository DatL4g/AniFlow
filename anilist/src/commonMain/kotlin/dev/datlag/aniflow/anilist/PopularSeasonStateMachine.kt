package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.common.nextSeason
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.model.CatchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class PopularSeasonStateMachine(
    private val client: ApolloClient,
    private val crashlytics: FirebaseFactory.Crashlytics?
) : FlowReduxStateMachine<PopularSeasonStateMachine.State, PopularSeasonStateMachine.Action>(
    initialState = currentState
) {

    init {
        spec {
            inState<State.Loading> {
                onEnterEffect {
                    currentState = it
                }
                onEnter { state ->
                    Cache.popularSeason.get(state.snapshot.query)?.let {
                        return@onEnter state.override { State.Success(query, it) }
                    }

                    val response = CatchResult.result {
                        client.query(state.snapshot.query).execute().dataOrThrow()
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
                    Cache.popularSeason.put(it.query, it.data)
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
            internal val query: SeasonQuery
        ) : State {
            constructor(
                page: Int,
                perPage: Int = 10,
                adultContent: Boolean = false,
                type: MediaType = MediaType.ANIME,
                season: MediaSeason = Clock.System.now().nextSeason.first,
                year: Int = Clock.System.now().nextSeason.second
            ) : this(
                SeasonQuery(
                    page = Optional.present(page),
                    perPage = Optional.present(perPage),
                    adultContent = if (!adultContent) {
                        Optional.present(adultContent)
                    } else {
                        Optional.absent()
                    },
                    type = Optional.present(type),
                    sort = Optional.present(listOf(MediaSort.POPULARITY_DESC)),
                    preventGenres = if (!adultContent) {
                        Optional.present(AdultContent.Genre.allTags)
                    } else {
                        Optional.absent()
                    },
                    year = Optional.present(year),
                    season = if (season == MediaSeason.UNKNOWN__) {
                        Optional.absent()
                    } else {
                        Optional.present(season)
                    }
                )
            )

            constructor(
                page: Int,
                perPage: Int = 10,
                adultContent: Boolean = false,
                type: MediaType = MediaType.ANIME,
                now: Instant = Clock.System.now()
            ) : this(
                page = page,
                perPage = perPage,
                adultContent = adultContent,
                type = type,
                season = now.nextSeason.first,
                year = now.nextSeason.second
            )
        }

        data class Success(
            internal val query: SeasonQuery,
            val data: SeasonQuery.Data
        ) : State

        data class Error(
            internal val query: SeasonQuery
        ) : State
    }

    sealed interface Action {
        data object Retry : Action
    }

    companion object {
        var currentState: PopularSeasonStateMachine.State
            get() = StateSaver.popularSeason
            set(value) {
                StateSaver.popularSeason = value
            }
    }
}