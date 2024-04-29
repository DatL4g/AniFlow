package dev.datlag.aniflow.other

import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularNextSeasonStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.anilist.state.SeasonState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

data object StateSaver {
    var sekretLibraryLoaded: Boolean = false

    data object List {
        var homeOverview: Int = 0
        var homeOverviewOffset: Int = 0

        var mediaOverview: Int = 0
        var mediaOverviewOffset: Int = 0

        var settingsOverview: Int = 0
        var settingsOverviewOffset: Int = 0

        data object Home {
            var airingOverview: Int = 0
            var airingOverviewOffset: Int = 0

            var trendingOverview: Int = 0
            var trendingOverviewOffset: Int = 0

            var popularOverview: Int = 0
            var popularOverviewOffset: Int = 0

            var popularNextOverview: Int = 0
            var popularNextOverviewOffset: Int = 0
        }
    }

    data object Home {
        private val _airingState = MutableStateFlow(airingState)
        private val _trendingState = MutableStateFlow(trendingState)
        private val _popularCurrentState = MutableStateFlow(popularCurrentState)
        private val _popularNextState = MutableStateFlow(popularNextState)

        val airingState: AiringTodayStateMachine.State
            get() = AiringTodayStateMachine.currentState

        val trendingState: TrendingAnimeStateMachine.State
            get() = TrendingAnimeStateMachine.currentState

        val popularCurrentState: SeasonState
            get() = PopularSeasonStateMachine.currentState

        val popularNextState: SeasonState
            get() = PopularNextSeasonStateMachine.currentState

        val currentAllLoading: Boolean
            get() = _airingState.value.isLoadingOrWaiting
                    && _trendingState.value.isLoadingOrWaiting
                    && _popularCurrentState.value.isLoadingOrWaiting
                    && _popularNextState.value.isLoadingOrWaiting

        val isAllLoading = combine(
            _airingState,
            _trendingState,
            _popularCurrentState,
            _popularNextState
        ) { t1, t2, t3, t4 ->
            t1.isLoadingOrWaiting && t2.isLoadingOrWaiting && t3.isLoadingOrWaiting && t4.isLoadingOrWaiting
        }

        fun updateAiring(state: AiringTodayStateMachine.State) = _airingState.updateAndGet {
            state
        }

        fun updateTrending(state: TrendingAnimeStateMachine.State) = _trendingState.updateAndGet {
            state
        }

        fun updatePopularCurrent(state: SeasonState) = _popularCurrentState.updateAndGet {
            state
        }

        fun updatePopularNext(state: SeasonState) = _popularNextState.updateAndGet {
            state
        }
    }
}