package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.screen.initial.medium.MediumScreenComponent
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

    private val popularSeasonStateMachine by di.instance<PopularSeasonStateMachine>()
    override val popularSeasonState: StateFlow<PopularSeasonStateMachine.State> = popularSeasonStateMachine.state.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = PopularSeasonStateMachine.currentState
    )

    private val navigation = SlotNavigation<HomeConfig>()
    override val child: Value<ChildSlot<HomeConfig, Component>> = childSlot(
        source = navigation,
        serializer = HomeConfig.serializer()
    ) { config, context ->
        when (config) {
            is HomeConfig.Details -> MediumScreenComponent(
                componentContext = context,
                di = di,
                initialMedium = config.medium,
                onBack = navigation::dismiss
            )
        }
    }

    @Composable
    override fun render() {
        onRender {
            HomeScreen(this)
        }
    }

    override fun dismissContent() {

    }

    override fun details(medium: Medium) {
        navigation.activate(HomeConfig.Details(medium))
    }
}