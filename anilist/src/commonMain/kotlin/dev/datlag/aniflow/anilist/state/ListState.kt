package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.ListQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.type.MediaListStatus

sealed interface ListState {

    data object None : ListState

    sealed interface Data : ListState {
        val hasNextPage: Boolean
        val collection: Collection<Medium>
    }

    data class Loading(
        internal val query: PageListQuery,
        internal val fallback: Boolean,
        override val collection: Collection<Medium>
    ) : Data {
        override val hasNextPage: Boolean = false

        fun fromGraphQL(response: ApolloResponse<ListQuery.Data>): ListState {
            val data = response.data

            return if (data == null) {
                if (fallback) {
                    Error(
                        throwable = response.exception,
                        collection = collection
                    )
                } else {
                    copy(fallback = true)
                }
            } else {
                val mediaList = data.Page?.mediaListFilterNotNull()

                if (mediaList.isNullOrEmpty()) {
                    if (fallback) {
                        Error(
                            throwable = response.exception,
                            collection = collection
                        )
                    } else {
                        copy(fallback = true)
                    }
                } else {
                    Success(
                        hasNextPage = data.Page.pageInfo?.hasNextPage ?: false,
                        collection = (collection + mediaList.mapNotNull {
                            Medium(
                                media = it.media ?: return@mapNotNull null,
                                list = it
                            )
                        }).distinctBy { it.id }
                    )
                }
            }
        }
    }

    private sealed interface PostLoading : Data

    data class Success(
        override val hasNextPage: Boolean,
        override val collection: Collection<Medium>
    ) : PostLoading

    data class Error(
        internal val throwable: Throwable?,
        override val collection: Collection<Medium>
    ) : PostLoading {
        override val hasNextPage: Boolean = false
    }
}

sealed interface ListAction {

    sealed interface Page : ListAction {

        data object Next : Page
    }

    sealed interface Type : ListAction {

        data object Anime : Type

        data object Manga : Type

        data object Toggle : Type
    }

    data class Status(internal val value: MediaListStatus) : ListAction
}