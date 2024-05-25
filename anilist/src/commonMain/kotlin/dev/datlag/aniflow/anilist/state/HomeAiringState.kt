package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.CacheMissException
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.AiringQuery
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.PageAiringQuery

import dev.datlag.aniflow.anilist.AiringQuery as PageAiringGraphQL

sealed interface HomeAiringState {

    val isLoading: Boolean
        get() = this !is PostLoading

    val isError: Boolean
        get() = this is Failure

    data object Loading : HomeAiringState

    private sealed interface PostLoading : HomeAiringState

    data class Success(
        val collection: Collection<PageAiringGraphQL.AiringSchedule>
    ) : PostLoading

    data class Failure(
        internal val throwable: Throwable?
    ) : PostLoading

    companion object {
        fun fromResponse(nsfw: Boolean, response: ApolloResponse<AiringQuery.Data>): HomeAiringState {
            val data = response.data

            return if (data == null) {
                if (response.hasNonCacheError()) {
                    Failure(throwable = response.exception)
                } else {
                    Loading
                }
            } else {
                val airingList = data.Page?.airingSchedulesFilterNotNull()?.mapNotNull {
                    if (nsfw) {
                        it
                    } else {
                        if (AdultContent.isAdultContent(it)) {
                            null
                        } else {
                            it
                        }
                    }
                }

                if (airingList.isNullOrEmpty()) {
                    Failure(throwable = response.exception)
                } else {
                    Success(airingList)
                }
            }
        }
    }
}