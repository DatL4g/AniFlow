package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.type.MediaListSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.tooling.safeSubList
import dev.datlag.tooling.safeSubSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transformLatest

class RecommendationRepository(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val user: Flow<User?>,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false)
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val type = viewManga.distinctUntilChanged().transformLatest {
        return@transformLatest if (it) {
            emit(MediaType.MANGA)
        } else {
            emit(MediaType.ANIME)
        }
    }.distinctUntilChanged()

    private val watchedQuery = combine(
        type.distinctUntilChanged(),
        user.mapNotNull { it?.id }.distinctUntilChanged()
    ) { t, u ->
        Query.Watched(
            type = t,
            userId = u
        )
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val fallbackWatched = watchedQuery.transformLatest {
        return@transformLatest emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                State.fromListGraphQL(data)
            } else {
                null
            }
        } else {
            State.fromListGraphQL(data)
        }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val watched = watchedQuery.transformLatest {
        return@transformLatest emitAll(client.query(it.toGraphQL()).toFlow())
    }.mapNotNull {
        val data = it.data
        if (data == null) {
            if (it.hasErrors()) {
                State.fromListGraphQL(data)
            } else {
                null
            }
        } else {
            State.fromListGraphQL(data)
        }
    }.transformLatest {
        return@transformLatest if (it is State.Watched.Error) {
            emitAll(fallbackWatched)
        } else {
            emit(it)
        }
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val list = combineTransform(
        type.distinctUntilChanged(),
        nsfw.distinctUntilChanged(),
        watched.distinctUntilChanged()
    ) { t, n, w ->
        return@combineTransform when (w) {
            is State.Watched.Success -> {
                val query = Query.Search(
                    type = t,
                    nsfw = n,
                    medium = w.medium
                )

                emitAll(
                    client.query(query.toGraphQL()).toFlow().mapNotNull {
                        val data = it.data
                        if (data == null) {
                            if (it.hasErrors()) {
                                State.fromSearchGraphQL(data)
                            } else {
                                null
                            }
                        } else {
                            State.fromSearchGraphQL(data)
                        }
                    }.transformLatest {
                        return@transformLatest if (it is State.Search.Error) {
                            emitAll(fallbackClient.query(query.toGraphQL()).toFlow().mapNotNull { f ->
                                val data = f.data
                                if (data == null) {
                                    if (f.hasErrors()) {
                                        State.fromSearchGraphQL(data)
                                    } else {
                                        null
                                    }
                                } else {
                                    State.fromSearchGraphQL(data)
                                }
                            })
                        } else {
                            emit(it)
                        }
                    }
                )
            }
            else -> emit(w)
        }
    }.distinctUntilChanged()

    private sealed interface Query {
        val type: MediaType

        data class Watched(
            override val type: MediaType,
            val userId: Int
        ) : Query {

            fun toGraphQL() = ListQuery(
                type = if (type == MediaType.UNKNOWN__) {
                    Optional.absent()
                } else {
                    Optional.present(type)
                },
                userId = userId,
                sort = Optional.present(listOf(MediaListSort.FINISHED_ON_DESC, MediaListSort.UPDATED_TIME_DESC)),
                statusVersion = 2,
                html = true
            )
        }

        data class Search(
            override val type: MediaType,
            val nsfw: Boolean,
            val medium: Collection<Medium>
        ) : Query {
            fun toGraphQL(): RecommendationQuery {
                val allGenres = medium.flatMap { m ->
                    if (m.isFavorite) {
                        m.genres.toList() + m.genres.toList()
                    } else {
                        m.genres.toList()
                    }
                }.toMutableList()

                if (!nsfw) {
                    AdultContent.Genre.allTags.forEach {
                        allGenres.remove(it)
                    }
                }

                val mostWatched = allGenres.groupingBy {
                    it
                }.eachCount().toList().sortedByDescending {
                    it.second
                }.safeSubList(0, 5).toMap().keys.safeSubSet(0, 5)

                return RecommendationQuery(
                    adultContent = if (nsfw) {
                        Optional.absent()
                    } else {
                        Optional.present(nsfw)
                    },
                    type = if (type == MediaType.UNKNOWN__) {
                        Optional.absent()
                    } else {
                        Optional.present(type)
                    },
                    wantedGenres = Optional.present(mostWatched.toList()),
                    preventGenres = if (nsfw) {
                        Optional.absent()
                    } else {
                        Optional.present(AdultContent.Genre.allTags)
                    },
                    preventIds = Optional.present(medium.map { it.id })
                )
            }
        }
    }

    sealed interface State {
        data object None : State

        sealed interface Watched : State {
            data class Success(val medium: Collection<Medium>) : Watched

            data object Error : Watched
        }

        sealed interface Search : State {
            data class Success(val medium: Collection<Medium>) : Search

            data object Error : Search
        }

        companion object {
            fun fromListGraphQL(query: ListQuery.Data?): State {
                val medium = query?.Page?.mediaListFilterNotNull()?.mapNotNull {
                    Medium(
                        media = it.media ?: return@mapNotNull null,
                        list = it
                    )
                } ?: return Watched.Error

                return Watched.Success(medium.distinctBy { it.id })
            }

            fun fromSearchGraphQL(query: RecommendationQuery.Data?): State {
                val medium = query?.Page?.mediaFilterNotNull()?.map {
                    Medium(it)
                } ?: return Search.Error

                return Search.Success(medium.distinctBy { it.id })
            }
        }
    }
}