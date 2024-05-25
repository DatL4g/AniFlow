package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import dev.datlag.aniflow.anilist.common.hasNonCacheError
import dev.datlag.aniflow.anilist.model.PageListQuery
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.state.ListState
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class ListStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val user: Flow<User?>,
    private val viewManga: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?,
) {

    var currentState: ListState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val page = MutableStateFlow(0)
    private val _type = MutableStateFlow(MediaType.UNKNOWN__)
    private val _status = MutableStateFlow(MediaListStatus.UNKNOWN__)
    val status: StateFlow<MediaListStatus> = _status

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

    private val query = combine(
        page,
        type,
        _status,
        user.mapNotNull { it?.id }.distinctUntilChanged()
    ) { p, t, s, u ->
        PageListQuery.ForPage(
            page = p,
            type = t,
            status = s,
            userId = u
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

    val list = combine(
        page,
        response
    ) { p, r ->
        p to r
    }.runningFold(initial = currentState) { accumulator, (p, r) ->
        return@runningFold (if (p <= 0) {
            ListState.fromResponse(persistentSetOf(), r)
        } else {
            ListState.fromResponse(accumulator, r)
        }).also { state ->
            (state as? ListState.Failure)?.throwable?.let {
                crashlytics?.log(it)
            }
            currentState = state
        }
    }

    fun nextPage() {
        page.update { it + 1 }
    }

    fun viewAnime() {
        page.update { 0 }
        _type.update { MediaType.ANIME }
    }

    fun viewManga() {
        page.update { 0 }
        _type.update { MediaType.MANGA }
    }

    fun toggleType() {
        page.update { 0 }
        _type.update {
            if (it == MediaType.MANGA) {
                MediaType.ANIME
            } else {
                MediaType.MANGA
            }
        }
    }

    fun status(value: MediaListStatus) {
        page.update { 0 }
        _status.update { value }
    }

    companion object {
        var currentState
            get() = StateSaver.listState
            private set(value) {
                StateSaver.listState = value
            }
    }
}