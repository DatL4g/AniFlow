package dev.datlag.aniflow.anilist.model

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.ListQuery
import dev.datlag.aniflow.anilist.common.presentAsList
import dev.datlag.aniflow.anilist.common.presentMediaListSort
import dev.datlag.aniflow.anilist.common.presentMediaListStatus
import dev.datlag.aniflow.anilist.common.presentMediaType
import dev.datlag.aniflow.anilist.type.MediaListSort
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType

sealed interface PageListQuery {

    fun toGraphQL(): ListQuery

    data class ForPage(
        val page: Int,
        val type: MediaType,
        val userId: Int,
        val status: MediaListStatus
    ) : PageListQuery {
        override fun toGraphQL(): ListQuery = ListQuery(
            page = Optional.present(page),
            perPage = Optional.present(20),
            userId = userId,
            type = Optional.presentMediaType(type),
            sort = Optional.presentMediaListSort(MediaListSort.UPDATED_TIME_DESC),
            status = Optional.presentMediaListStatus(status),
            statusVersion = 2,
            html = true
        )
    }

    data class Recommendation(
        val type: MediaType,
        val userId: Int
    ) : PageListQuery {
        override fun toGraphQL(): ListQuery = ListQuery(
            type = Optional.presentMediaType(type),
            userId = userId,
            sort = Optional.presentAsList(
                MediaListSort.FINISHED_ON_DESC,
                MediaListSort.UPDATED_TIME_DESC
            ),
            statusVersion = 2,
            html = true
        )
    }
}