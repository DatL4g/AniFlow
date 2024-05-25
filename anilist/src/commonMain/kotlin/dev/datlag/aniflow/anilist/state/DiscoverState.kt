package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.ListQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.PageMediaQuery as PageMediaGraphQL

sealed interface DiscoverState {
    sealed interface Recommended : DiscoverState {
        data object None : Recommended

        sealed interface Loading : Recommended {
            data class WatchList(
                internal val query: PageListQuery,
                internal val fallback: Boolean
            ) : Loading {


                fun fromGraphQL(nsfw: Boolean, response: ApolloResponse<ListQuery.Data>): DiscoverState {
                    val data = response.data

                    return if (data == null) {
                        if (fallback) {
                            Error(throwable = response.exception)
                        } else {
                            copy(fallback = true)
                        }
                    } else {
                        val mediumList = data.Page?.mediaListFilterNotNull()?.mapNotNull {
                            Medium(
                                media = it.media ?: return@mapNotNull null,
                                list = it
                            )
                        }?.distinctBy { it.id }

                        if (mediumList.isNullOrEmpty()) {
                            if (fallback) {
                                Error(throwable = response.exception)
                            } else {
                                copy(fallback = true)
                            }
                        } else {
                            Matching(
                                query = PageMediaQuery.Recommendation(
                                    nsfw = nsfw,
                                    collection = mediumList
                                ),
                                fallback = false
                            )
                        }
                    }
                }
            }

            data class Matching(
                internal val query: PageMediaQuery,
                internal val fallback: Boolean
            ) : Loading {
                fun fromGraphQL(response: ApolloResponse<PageMediaGraphQL.Data>): DiscoverState {
                    val data = response.data

                    return if (data == null) {
                        if (fallback) {
                            Error(throwable = response.exception)
                        } else {
                            copy(fallback = true)
                        }
                    } else {
                        val mediumList = data.Page?.mediaFilterNotNull()

                        if (mediumList.isNullOrEmpty()) {
                            if (fallback) {
                                Error(throwable = response.exception)
                            } else {
                                copy(fallback = true)
                            }
                        } else {
                            Success(
                                collection = mediumList.map(::Medium).distinctBy { it.id }
                            )
                        }
                    }
                }
            }
        }
    }

    sealed interface Season : DiscoverState {

        data class None(internal val wanted: MediaSeason) : Season

        data class Loading(
            internal val query: PageMediaQuery,
            internal val fallback: Boolean
        ) : Season {
            fun fromGraphQL(response: ApolloResponse<PageMediaGraphQL.Data>): DiscoverState {
                val data = response.data

                return if (data == null) {
                    if (fallback) {
                        Error(throwable = response.exception)
                    } else {
                        copy(fallback = true)
                    }
                } else {
                    val mediumList = data.Page?.mediaFilterNotNull()

                    if (mediumList.isNullOrEmpty()) {
                        if (fallback) {
                            Error(throwable = response.exception)
                        } else {
                            copy(fallback = true)
                        }
                    } else {
                        Success(
                            collection = mediumList.map(::Medium).distinctBy { it.id }
                        )
                    }
                }
            }
        }
    }

    private sealed interface PostLoading : DiscoverState

    data class Success(
        val collection: Collection<Medium>
    ) : PostLoading

    data class Error(
        internal val throwable: Throwable?
    ) : PostLoading
}

sealed interface DiscoverAction {

    sealed interface Type : DiscoverAction {

        data object Anime : Type

        data object Manga : Type

        data object Toggle : Type
    }

    sealed interface ListType : DiscoverAction {

        data object Recommendation : ListType

        sealed interface Season : ListType {
            val mediaSeason : MediaSeason
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
    }
}