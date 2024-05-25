package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest

@OptIn(ExperimentalCoroutinesApi::class)
class TrendingStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean>,
    private val viewManga: Flow<Boolean>,
    private val crashlytics: FirebaseFactory.Crashlytics?
) {

    var currentState: HomeDefaultState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val type = viewManga.mapLatest {
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
        }
    }.distinctUntilChanged()

    private val query = combine(
        type,
        nsfw.distinctUntilChanged()
    ) { t, n ->
        PageMediaQuery.Trending(
            type = t,
            nsfw = n
        )
    }.distinctUntilChanged()

    private val fallbackResponse = query.transform {
        return@transform emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }

    private val response = query.transform {
        return@transform emitAll(client.query(it.toGraphQL()).toFlow())
    }.transform {
        return@transform if (it.hasNonCacheError()) {
            emitAll(fallbackResponse)
        } else {
            emit(it)
        }
    }

    val trending = response.map { result ->
        HomeDefaultState.fromResponse(result).also { state ->
            (state as? HomeDefaultState.Failure)?.throwable?.let {
                crashlytics?.log(it)
            }
            currentState = state
        }
    }

    companion object {
        var currentState: HomeDefaultState
            get() = StateSaver.trendingState
            private set(value) {
                StateSaver.trendingState = value
            }
    }
}
