package dev.datlag.aniflow.other

import androidx.compose.ui.graphics.Color
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
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

        private val mediumOverview = mutableMapOf<Int, Pair<Int, Int>>()

        fun mediumOverview(id: Int): Pair<Int, Int> = mediumOverview.getOrElse(id) { 0 to 0 }

        fun mediumOverview(id: Int, index: Int, offset: Int) {
            mediumOverview[id] = index to offset
        }

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
        private val popularCurrentLoading = MutableStateFlow(true)
        private val popularNextLoading = MutableStateFlow(true)

        val currentAllLoading: Boolean
            get() = airingLoading.value
                    && trendingLoading.value
                    && popularCurrentLoading.value
                    && popularNextLoading.value

        val isAllLoading = combine(
            airingLoading,
            trendingLoading,
            popularCurrentLoading,
            popularNextLoading
        ) { t1, t2, t3, t4 ->
            t1 && t2 && t3 && t4
        }.flowOn(ioDispatcher()).distinctUntilChanged()

        fun updateAiring(state: AiringTodayRepository.State): AiringTodayRepository.State {
            airingLoading.update { false }
            return state
        }

        fun updateTrending(state: CollectionState): CollectionState {
            trendingLoading.update { false }
            return state
        }

        fun updatePopularCurrent(state: CollectionState): CollectionState {
            popularCurrentLoading.update { false }
            return state
        }

        fun updatePopularNext(state: CollectionState): CollectionState {
            popularNextLoading.update { false }
            return state
        }

        fun updateAllLoading() {
            airingLoading.update { true }
            trendingLoading.update { true }
            popularCurrentLoading.update { true }
            popularNextLoading.update { true }
        }
    }
}