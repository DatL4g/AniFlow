package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.AiringSort
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class AiringTodayRepository(
    private val apolloClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    private val page = MutableStateFlow(0)
    private val query = combine(page, nsfw.distinctUntilChanged()) { p, n ->
        Query(
            page = p,
            nsfw = n,
        )
    }

    private val airingPreFilter = query.transform {
        return@transform emitAll(apolloClient.query(it.toGraphQL()).toFlow())
    }
    val airing = combine(airingPreFilter, nsfw) { q, n ->
        State.fromGraphQL(q.data, n)
    }

    fun nextPage() = page.getAndUpdate {
        it + 1
    }

    fun previousPage() = page.getAndUpdate {
        it - 1
    }

    private data class Query(
        val page: Int,
        val nsfw: Boolean
    ) {
        fun toGraphQL() = AiringQuery(
            page = Optional.present(page),
            perPage = Optional.present(20),
            sort = Optional.present(listOf(AiringSort.TIME)),
            airingAtGreater = Optional.present(
                Clock.System.now().minus(1.hours).epochSeconds.toInt()
            ),
            statusVersion = Optional.present(2),
            html = Optional.present(true)
        )
    }

    sealed interface State {
        data class Success(
            val collection: Collection<AiringQuery.AiringSchedule>
        ) : State

        data object Error : State

        companion object {
            fun fromGraphQL(data: AiringQuery.Data?, nsfw: Boolean): State {
                val airingList = data?.Page?.airingSchedulesFilterNotNull()?.mapNotNull {
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
                    return Error
                }

                return Success(airingList)
            }
        }
    }
}