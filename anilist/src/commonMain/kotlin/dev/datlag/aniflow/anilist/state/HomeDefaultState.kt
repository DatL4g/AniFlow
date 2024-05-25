package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.PageMediaQuery
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.Medium

sealed interface HomeDefaultState {

    val isLoading: Boolean
        get() = this !is PostLoading

    val isError: Boolean
        get() = this is Failure

    data object Loading : HomeDefaultState

    private sealed interface PostLoading : HomeDefaultState

    data class Success(val collection: Collection<Medium>) : PostLoading

    data class Failure(
        internal val throwable: Throwable?
    ) : PostLoading

    companion object {
        fun fromResponse(response: ApolloResponse<PageMediaQuery.Data>): HomeDefaultState {
            val data = response.data

            return if (data == null) {
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
