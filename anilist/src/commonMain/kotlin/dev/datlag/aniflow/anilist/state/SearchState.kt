package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.PageMediaQuery
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.Medium

sealed interface SearchState {

    val isLoading: Boolean
        get() = this !is PostLoading

    val isError: Boolean
        get() = this is Failure

    data object None : SearchState

    data object Loading : SearchState

    private sealed interface PostLoading : SearchState

    data class Success(
        val collection: Collection<Medium>
    ) : PostLoading

    data class Failure(
        internal val throwable: Throwable?
    ) : PostLoading

    companion object {
        fun fromResponse(response: ApolloResponse<PageMediaQuery.Data>?): SearchState {
            return if (response == null) {
                None
            } else {
                val data = response.data

                if (data == null) {
                    if (response.hasNonCacheError()) {
                        Failure(response.exception)
                    } else {
                        Loading
                    }
                } else {
                    val mediumList = data.Page?.mediaFilterNotNull()

                    if (mediumList.isNullOrEmpty()) {
                        Failure(response.exception)
                    } else {
                        Success(mediumList.map(::Medium))
                    }
                }
            }
        }
    }
}
