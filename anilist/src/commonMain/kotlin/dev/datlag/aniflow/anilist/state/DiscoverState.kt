package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.ListQuery
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.type.MediaSeason
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import dev.datlag.aniflow.anilist.PageMediaQuery as PageMediaGraphQL

sealed interface DiscoverState {

    val isFailure: Boolean
        get() = this is Failure

    sealed interface Recommended : DiscoverState {

        sealed interface Loading : Recommended {
            data object WatchList : Loading

            data class Matching(
                internal val query: PageMediaQuery
            ) : Loading

            companion object {
                fun fromList(nsfw: Boolean, response: ApolloResponse<ListQuery.Data>): DiscoverState {
                    val data = response.data

                    return if (data == null) {
                        if (response.hasNonCacheError()) {
                            Failure(response.exception)
                        } else {
                            WatchList
                        }
                    } else {
                        val mediumList = data.Page?.mediaListFilterNotNull()?.mapNotNull {
                            Medium(
                                media = it.media ?: return@mapNotNull null,
                                list = it
                            )
                        }?.distinctBy { it.id }

                        if (mediumList.isNullOrEmpty()) {
                            Failure(response.exception)
                        } else {
                            Matching(
                                query = PageMediaQuery.Recommendation(
                                    nsfw = nsfw,
                                    collection = mediumList.toImmutableList()
                                )
                            )
                        }
                    }
                }

                fun fromMatching(defaultState: DiscoverState, response: ApolloResponse<PageMediaGraphQL.Data>): DiscoverState {
                    val data = response.data

                    return if (data == null) {
                        if (response.hasNonCacheError()) {
                            Failure(response.exception)
                        } else {
                            defaultState
                        }
                    } else {
                        val mediumList = data.Page?.mediaFilterNotNull()

                        if (mediumList.isNullOrEmpty()) {
                            Failure(response.exception)
                        } else {
                            Success(
                                collection = mediumList.map(::Medium).distinctBy { it.id }.toImmutableList()
                            )
                        }
                    }
                }
            }
        }
    }

    sealed interface Season : DiscoverState {

        data object Loading : Season

        companion object {
            fun fromSeasonResponse(response: ApolloResponse<PageMediaGraphQL.Data>): DiscoverState {
                val data = response.data

                return if (data == null) {
                    if (response.hasNonCacheError()) {
                        Failure(throwable = response.exception)
                    } else {
                        Loading
                    }
                } else {
                    val mediumList = data.Page?.mediaFilterNotNull()

                    if (mediumList.isNullOrEmpty()) {
                        Failure(throwable = response.exception)
                    } else {
                        Success(
                            collection = mediumList.map(::Medium).distinctBy { it.id }.toImmutableList()
                        )
                    }
                }
            }
        }
    }

    private sealed interface PostLoading : DiscoverState

    data class Success(
        val collection: ImmutableCollection<Medium>
    ) : PostLoading

    data class Failure(
        internal val throwable: Throwable?
    ) : PostLoading
}

sealed interface DiscoverListType {

    data object Recommendation : DiscoverListType

    sealed interface Season : DiscoverListType {
        val mediaSeason : MediaSeason

        companion object {
            fun fromSeason(season: MediaSeason): Season = when (season) {
                MediaSeason.SPRING -> Spring
                MediaSeason.SUMMER -> Summer
                MediaSeason.FALL -> Fall
                MediaSeason.WINTER -> Winter
                else -> Spring
            }
        }
    }

    data object Spring : Season {
        override val mediaSeason: MediaSeason = MediaSeason.SPRING
    }

    data object Summer : Season {
        override val mediaSeason: MediaSeason = MediaSeason.SUMMER
    }

    data object Fall : Season {
        override val mediaSeason: MediaSeason = MediaSeason.FALL
    }

    data object Winter : Season {
        override val mediaSeason: MediaSeason = MediaSeason.WINTER
    }

    companion object {
        val entries = persistentSetOf(
            DiscoverListType.Recommendation,
            DiscoverListType.Spring,
            DiscoverListType.Summer,
            DiscoverListType.Fall,
            DiscoverListType.Winter
        )
    }
}