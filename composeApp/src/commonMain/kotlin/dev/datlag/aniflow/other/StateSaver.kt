package dev.datlag.aniflow.other

import androidx.compose.ui.graphics.Color
import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.compose.ioDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds
import dev.datlag.aniflow.settings.model.Color as SettingsColor

data object StateSaver {
    var sekretLibraryLoaded: Boolean = false
    val temporaryColor = MutableStateFlow<SettingsColor?>(null)

    fun updateTemporaryColor(value: SettingsColor?) = temporaryColor.update { value }

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
        private val airingLoading = MutableStateFlow(true)
        private val trendingLoading = MutableStateFlow(true)
        private val _popularCurrentState = MutableStateFlow(popularCurrentState)
        private val _popularNextState = MutableStateFlow(popularNextState)

        val popularCurrentState: SeasonState
            get() = PopularSeasonStateMachine.currentState

        val popularNextState: SeasonState
            get() = PopularNextSeasonStateMachine.currentState

        val currentAllLoading: Boolean
            get() = airingLoading.value
                    && trendingLoading.value
                    && _popularCurrentState.value.isLoadingOrWaiting
                    && _popularNextState.value.isLoadingOrWaiting

        val isAllLoading = combine(
            airingLoading,
            trendingLoading,
            _popularCurrentState,
            _popularNextState
        ) { t1, t2, t3, t4 ->
            t1 && t2 && t3.isLoadingOrWaiting && t4.isLoadingOrWaiting
        }.flowOn(ioDispatcher()).distinctUntilChanged()

        fun updateAiring(state: AiringTodayRepository.State): AiringTodayRepository.State {
            airingLoading.update { false }
            return state
        }

        fun updateTrending(state: TrendingRepository.State): TrendingRepository.State {
            trendingLoading.update { false }
            return state
        }

        fun updatePopularCurrent(state: SeasonState) = _popularCurrentState.updateAndGet {
            state
        }

        fun updatePopularNext(state: SeasonState) = _popularNextState.updateAndGet {
            state
        }
    }
}