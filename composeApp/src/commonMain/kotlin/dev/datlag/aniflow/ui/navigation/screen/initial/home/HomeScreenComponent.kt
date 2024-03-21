package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.common.onRender
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.kodein.di.DI
import org.kodein.di.instance

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : HomeComponent, ComponentContext by componentContext {

    private val airingTodayStateMachine by di.instance<AiringTodayStateMachine>()
    override val airingState: StateFlow<AiringTodayStateMachine.State> = airingTodayStateMachine.state.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = AiringTodayStateMachine.currentState
    )

    private val trendingAnimeStateMachine by di.instance<TrendingAnimeStateMachine>()
    override val trendingState: StateFlow<TrendingAnimeStateMachine.State> = trendingAnimeStateMachine.state.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = TrendingAnimeStateMachine.currentState
    )

    @Composable
    override fun render() {
        onRender {
            HomeScreen(this)
        }
    }

    override fun dismissContent() {

    }
}