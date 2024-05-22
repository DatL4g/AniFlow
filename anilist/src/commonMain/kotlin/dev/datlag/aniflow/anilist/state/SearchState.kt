package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.PageMediaQuery as PageMediaGraphQL

sealed interface SearchState {

    val isLoading: Boolean
        get() = this !is PostLoading

    val isError: Boolean
        get() = this is Error

    data object None : SearchState

    data class Loading(
        internal val query: PageMediaQuery.Search,
        internal val fallback: Boolean
    ) : SearchState {

        fun fromGraphQL(response: ApolloResponse<PageMediaGraphQL.Data>): SearchState {
            val data = response.data

            return if (data == null) {
                if (fallback) {
                    Error(throwable = response.exception)
                } else {
                    copy(fallback = true)
                }
            } else {
                val mediaList = data.Page?.mediaFilterNotNull()

                if (mediaList.isNullOrEmpty()) {
                    if (fallback) {
                        Error(throwable = response.exception)
                    } else {
                        copy(fallback = true)
                    }
                } else {
                    Success(mediaList.map(::Medium))
                }
            }
        }
    }

    private sealed interface PostLoading : SearchState

    data class Success(
        val collection: Collection<Medium>
    ) : PostLoading

    data class Error(
        internal val throwable: Throwable?
    ) : PostLoading
}

sealed interface SearchAction {

    sealed interface Type : SearchAction {

        data object Anime : Type

        data object Manga : Type

        data object Toggle : Type
    }
}