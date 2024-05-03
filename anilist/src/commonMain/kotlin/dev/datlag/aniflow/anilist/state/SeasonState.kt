package dev.datlag.aniflow.anilist.state

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.SeasonQuery
import dev.datlag.aniflow.anilist.common.season
import dev.datlag.aniflow.anilist.common.year
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

sealed interface SeasonState {
    data class Success(
        val collection: Collection<Medium>
    ) : SeasonState

    data object Error : SeasonState

    companion object {
        fun fromGraphQL(data: SeasonQuery.Data?): SeasonState {
            val mediaList = data?.Page?.mediaFilterNotNull()

            if (mediaList.isNullOrEmpty()) {
                return Error
            }

            return Success(mediaList.map { Medium(it) })
        }
    }
}