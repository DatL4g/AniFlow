package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest

@OptIn(ExperimentalCoroutinesApi::class)
class PopularNextSeasonStateMachine(
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
        PageMediaQuery.PopularNextSeason(
            type = t,
            nsfw = n
        )
    }.distinctUntilChanged()

    private val fallbackResponse = query.transformLatest {
        return@transformLatest emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }

    private val response = query.transformLatest {
        return@transformLatest emitAll(client.query(it.toGraphQL()).toFlow())
    }.transformLatest {
        return@transformLatest if (it.hasNonCacheError()) {
            emitAll(fallbackResponse)
        } else {
            emit(it)
        }
    }

    val popularNext = response.map { result ->
        HomeDefaultState.fromResponse(result).also { state ->
            (state as? HomeDefaultState.Failure)?.throwable?.let {
                crashlytics?.log(it)
            }
            currentState = state
        }
    }

    companion object {
        var currentState: HomeDefaultState
            get() = StateSaver.popularNextSeasonState
            private set(value) {
                StateSaver.popularNextSeasonState = value
            }
    }
}