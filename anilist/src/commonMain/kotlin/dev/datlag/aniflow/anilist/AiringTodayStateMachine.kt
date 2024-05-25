package dev.datlag.aniflow.anilist

import com.apollographql.apollo3.ApolloClient
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.aniflow.anilist.model.PageAiringQuery
import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.firebase.FirebaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest

@OptIn(ExperimentalCoroutinesApi::class)
class AiringTodayStateMachine(
    private val client: ApolloClient,
    private val fallbackClient: ApolloClient,
    private val nsfw: Flow<Boolean> = flowOf(false),
    private val crashlytics: FirebaseFactory.Crashlytics?
) {

    var currentState: HomeAiringState
        get() = Companion.currentState
        private set(value) {
            Companion.currentState = value
        }

    private val fallbackResponse = fallbackClient.query(PageAiringQuery.Today.toGraphQL()).toFlow()

    private val response = client.query(PageAiringQuery.Today.toGraphQL()).toFlow().transformLatest {
        return@transformLatest if (it.hasErrors()) {
            emitAll(fallbackResponse)
        } else {
            emit(it)
        }
    }

    val airing = combine(
        response,
        nsfw.distinctUntilChanged()
    ) { r, n ->
        HomeAiringState.fromResponse(n, r).also { state ->
            (state as? HomeAiringState.Failure)?.throwable?.let {
                crashlytics?.log(it)
            }
            currentState = state
        }
    }

    companion object {
        var currentState: HomeAiringState
            get() = StateSaver.airingState
            private set(value) {
                StateSaver.airingState = value
            }
    }
}