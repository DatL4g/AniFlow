package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularNextSeasonStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.trace.TraceStateMachine
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumScreenComponent
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.time.Duration.Companion.seconds

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onMediumDetails: (Medium) -> Unit
) : HomeComponent, ComponentContext by componentContext {

    private val airingTodayStateMachine by di.instance<AiringTodayStateMachine>()
    override val airingState: Flow<AiringTodayStateMachine.State> = airingTodayStateMachine.state.map {
        StateSaver.Home.updateAiring(it)
    }.flowOn(
        context = ioDispatcher()
    )

    private val trendingAnimeStateMachine by di.instance<TrendingAnimeStateMachine>()
    override val trendingState: Flow<TrendingAnimeStateMachine.State> = trendingAnimeStateMachine.state.map {
        StateSaver.Home.updateTrending(it)
    }.flowOn(
        context = ioDispatcher()
    )

    private val popularSeasonStateMachine by di.instance<PopularSeasonStateMachine>()
    override val popularSeasonState: Flow<SeasonState> = popularSeasonStateMachine.state.map {
        StateSaver.Home.updatePopularCurrent(it)
    }.flowOn(
        context = ioDispatcher()
    )

    private val popularNextSeasonStateMachine by di.instance<PopularNextSeasonStateMachine>()
    override val popularNextSeasonState: Flow<SeasonState> = popularNextSeasonStateMachine.state.map {
        StateSaver.Home.updatePopularNext(it)
    }.flowOn(
        context = ioDispatcher()
    )

    private val traceStateMachine by di.instance<TraceStateMachine>()
    override val traceState: Flow<TraceStateMachine.State> = traceStateMachine.state.flowOn(
        context = ioDispatcher()
    )

    @Composable
    override fun render() {
        onRender {
            HomeScreen(this)
        }
    }

    override fun dismissContent() {

    }

    override fun details(medium: Medium) {
        onMediumDetails(medium)
    }

    override fun trace(channel: ByteArray) {
        launchIO {
            traceStateMachine.dispatch(TraceStateMachine.Action.Load(channel))
        }
    }
}