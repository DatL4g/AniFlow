package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.common.season
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.model.PageMediaQuery
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.anilist.state.DiscoverListType
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val user: Flow<User?>,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val viewManga: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?,
) {

    var currentState: DiscoverState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    val currentListType: DiscoverListType
        get() = _listType.value

    private val _type: MutableStateFlow<MediaType> = MutableStateFlow(MediaType.UNKNOWN__)
    val type = _type.transformLatest {
        return@transformLatest if (it == MediaType.UNKNOWN__) {
            emitAll(viewManga.map { m ->
                if (m) {
                    MediaType.MANGA
                } else {
                    MediaType.ANIME
                }
            })
        } else {
            emit(it)
        }
    }.distinctUntilChanged()

    private val _listType: MutableStateFlow<DiscoverListType> = MutableStateFlow(DiscoverListType.Recommendation)
    val listType = combine(
        _listType,
        user.map { it?.id }.distinctUntilChanged()
    ) { l, u ->
        if (l is DiscoverListType.Recommendation) {
            if (u == null) {
                DiscoverListType.Season.fromSeason(Clock.System.now().season)
            } else {
                l
            }
        } else {
            l
        }
    }.distinctUntilChanged()

    private val recommendationQuery = combine(
        type,
        user.mapNotNull { it?.id }.distinctUntilChanged()
    ) { t, u ->
        PageListQuery.Recommendation(
            type = t,
            userId = u
        )
    }.distinctUntilChanged()

    private val fallbackRecommendationResponse = combine(
        recommendationQuery.transformLatest {
            return@transformLatest emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
        },
        nsfw.distinctUntilChanged()
    ) { r, n ->
        DiscoverState.Recommended.Loading.fromList(n, r)
    }.transformLatest {
        return@transformLatest when (it) {
            is DiscoverState.Recommended.Loading.Matching -> {
                emitAll(fallbackClient.query(it.query.toGraphQL()).toFlow().mapLatest { r ->
                    DiscoverState.Recommended.Loading.fromMatching(it, r)
                })
            }
            else -> emit(it)
        }
    }

    private val recommendationResponse = combine(
        recommendationQuery.transformLatest {
            return@transformLatest emitAll(client.query(it.toGraphQL()).toFlow())
        },
        nsfw.distinctUntilChanged()
    ) { r, n ->
        DiscoverState.Recommended.Loading.fromList(n, r)
    }.transformLatest {
        return@transformLatest if (it.isFailure) {
            emitAll(fallbackRecommendationResponse)
        } else {
            when (it) {
                is DiscoverState.Recommended.Loading.Matching -> {
                    emitAll(client.query(it.query.toGraphQL()).toFlow().mapLatest { r ->
                        DiscoverState.Recommended.Loading.fromMatching(it, r)
                    })
                }
                else -> emit(it)
            }
        }
    }

    private fun seasonQuery(season: MediaSeason) = nsfw.distinctUntilChanged().mapLatest { n ->
        PageMediaQuery.Season(
            season = season,
            nsfw = n
        )
    }.distinctUntilChanged()

    private fun fallbackSeasonResponse(season: MediaSeason) = seasonQuery(season).transformLatest {
        return@transformLatest emitAll(fallbackClient.query(it.toGraphQL()).toFlow())
    }

    private fun seasonResponse(season: MediaSeason) = seasonQuery(season).transformLatest {
        return@transformLatest emitAll(client.query(it.toGraphQL()).toFlow())
    }.transformLatest {
        return@transformLatest if (it.hasNonCacheError()) {
            emitAll(fallbackSeasonResponse(season))
        } else {
            emit(it)
        }
    }.mapLatest { result ->
        DiscoverState.Season.fromSeasonResponse(result)
    }

    val state = listType.transformLatest { type ->
        return@transformLatest when (type) {
            is DiscoverListType.Recommendation -> emitAll(recommendationResponse)
            is DiscoverListType.Season -> emitAll(seasonResponse(type.mediaSeason))
        }
    }.mapLatest { m ->
        m.also { state ->
            (state as? DiscoverState.Failure)?.throwable?.let {
                crashlytics?.log(it)
            }
            currentState = state
        }
    }

    fun viewAnime() {
        _type.update { MediaType.ANIME }
    }

    fun viewManga() {
        _type.update { MediaType.MANGA }
    }

    fun toggleType() {
        _type.update {
            if (it == MediaType.MANGA) {
                MediaType.ANIME
            } else {
                MediaType.MANGA
            }
        }
    }

    fun listType(type: DiscoverListType) {
        _listType.update { type }
    }

    companion object {
        var currentState: DiscoverState
            get() = StateSaver.discoverState
            private set(value) {
                StateSaver.discoverState = value
            }
    }
}