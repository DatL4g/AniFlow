package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.ApolloResponse
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.model.PageAiringQuery

import dev.datlag.aniflow.anilist.AiringQuery as PageAiringGraphQL

sealed interface HomeAiringState {

    val isLoading: Boolean
        get() = this !is PostLoading

    val isError: Boolean
        get() = this is Error

    data object None : HomeAiringState

    data class Loading(
        internal val query: PageAiringQuery,
        internal val fallback: Boolean
    ) : HomeAiringState {

        private val nsfw: Boolean
            get() = query.nsfw

        fun fromGraphQL(response: ApolloResponse<PageAiringGraphQL.Data>): HomeAiringState {
            val data = response.data

            return if (data == null) {
                if (fallback) {
                    Error(throwable = response.exception)
                } else {
                    copy(fallback = true)
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
                    if (fallback) {
                        Error(throwable = response.exception)
                    } else {
                        copy(fallback = true)
                    }
                } else {
                    Success(airingList)
                }
            }
        }
    }

    private sealed interface PostLoading : HomeAiringState

    data class Success(
        val collection: Collection<PageAiringGraphQL.AiringSchedule>
    ) : PostLoading

    data class Error(
        internal val throwable: Throwable?
    ) : PostLoading
}