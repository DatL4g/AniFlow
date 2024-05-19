package dev.datlag.aniflow.anilist.state

import dev.datlag.aniflow.anilist.SearchQuery
import dev.datlag.aniflow.anilist.SeasonQuery
import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import kotlinx.serialization.Serializable

@Serializable
sealed interface CollectionState {

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    @Serializable
    data object None : CollectionState

    @Serializable
    data class Success(
        val collection: Collection<Medium>
    ) : CollectionState

    @Serializable
    data object Error : CollectionState

    companion object {
        fun fromTrendingGraphQL(data: TrendingQuery.Data?): CollectionState {
            val mediaList = data?.Page?.mediaFilterNotNull()

            if (mediaList.isNullOrEmpty()) {
                return Error
            }

            return Success(mediaList.map { Medium(it) })
        }

        fun fromSeasonGraphQL(data: SeasonQuery.Data?): CollectionState {
            val mediaList = data?.Page?.mediaFilterNotNull()

            if (mediaList.isNullOrEmpty()) {
                return Error
            }

            return Success(mediaList.map { Medium(it) })
        }

        fun fromSearchGraphQL(data: SearchQuery.Data?): CollectionState {
            val mediaList = data?.Page?.mediaFilterNotNull()

            if (mediaList.isNullOrEmpty()) {
                return Error
            }

            return Success(mediaList.map { Medium(it) })
        }
    }
}

val CollectionState?.isLoading: Boolean
    get() = this == null || this is CollectionState.None