package dev.datlag.aniflow.anilist.model

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.AiringQuery
import dev.datlag.aniflow.anilist.common.presentAsList
import dev.datlag.aniflow.anilist.type.AiringSort
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours
import dev.datlag.aniflow.anilist.AiringQuery as AiringGraphQL

sealed interface PageAiringQuery {

    fun toGraphQL(): AiringGraphQL

    data object Today : PageAiringQuery {
        override fun toGraphQL() = AiringQuery(
            perPage = Optional.present(20),
            sort = Optional.presentAsList(AiringSort.TIME),
            airingAtGreater = Optional.present(
                Clock.System.now().minus(1.hours).epochSeconds.toInt()
            ),
            statusVersion = 2,
            html = true
        )
    }
}