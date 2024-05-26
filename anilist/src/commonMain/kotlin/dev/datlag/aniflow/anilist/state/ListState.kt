package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.ListQuery
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.Medium
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

sealed interface ListState {

    val hasNextPage: Boolean
    val collection: ImmutableCollection<Medium>

    data class Loading(
        override val collection: ImmutableCollection<Medium>
    ) : ListState {
        override val hasNextPage: Boolean = false
    }

    private sealed interface PostLoading : ListState

    data object Empty : PostLoading {
        override val hasNextPage: Boolean = false

        override val collection: ImmutableCollection<Medium> = persistentSetOf()
    }

    data class Success(
        override val hasNextPage: Boolean,
        override val collection: ImmutableCollection<Medium>
    ) : PostLoading

    data class Failure(
        internal val throwable: Throwable?,
        override val collection: ImmutableCollection<Medium>
    ) : PostLoading {
        override val hasNextPage: Boolean = false
    }

    companion object {

        fun fromResponse(
            previousState: ListState,
            response: ApolloResponse<ListQuery.Data>
        ) = fromResponse(
            previousCollection = previousState.collection,
            response = response
        )

        fun fromResponse(
            previousCollection: ImmutableCollection<Medium>,
            response: ApolloResponse<ListQuery.Data>
        ): ListState {
            val data = response.data

            return if (data == null) {
                if (response.hasNonCacheError()) {
                    Failure(
                        throwable = response.exception,
                        collection = previousCollection
                    )
                } else {
                    Loading(previousCollection)
                }
            } else {
                val mediaList = data.Page?.mediaListFilterNotNull()

                if (mediaList.isNullOrEmpty()) {
                    Empty
                } else {
                    Success(
                        hasNextPage = data.Page.pageInfo?.hasNextPage ?: false,
                        collection = (previousCollection + mediaList.mapNotNull {
                            Medium(
                                media = it.media ?: return@mapNotNull null,
                                list = it
                            )
                        }).distinctBy { it.id }.toImmutableList()
                    )
                }
            }
        }
    }
}
