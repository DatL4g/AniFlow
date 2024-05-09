package dev.datlag.aniflow.ui.navigation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.AiringTodayRepository
import dev.datlag.aniflow.anilist.PopularNextSeasonRepository
import dev.datlag.aniflow.anilist.PopularSeasonRepository
import dev.datlag.aniflow.anilist.TrendingRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.coroutines.Executor
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.trace.TraceRepository
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.kodein.di.DI
import org.kodein.di.instance

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onMediumDetails: (Medium) -> Unit,
    private val onProfile: () -> Unit,
    private val onDiscover: () -> Unit,
    private val onFavorites: () -> Unit
) : HomeComponent, ComponentContext by componentContext {

    private val appSettings by instance<Settings.PlatformAppSettings>()
    override val viewing = appSettings.viewManga.map {
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
        }
    }
    private val viewTypeExecutor = Executor()
    private val stateScope = ioScope()

    private val airingTodayRepository by instance<AiringTodayRepository>()
    override val airing: Flow<AiringTodayRepository.State> = airingTodayRepository.airing.map {
        StateSaver.Home.updateAiring(it)
    }.stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AiringTodayRepository.State.None
    )

    private val trendingRepository by instance<TrendingRepository>()
    override val trending: Flow<CollectionState> = trendingRepository.trending.map {
        StateSaver.Home.updateTrending(it)
    }.stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CollectionState.None
    )

    private val popularSeasonRepository by instance<PopularSeasonRepository>()
    override val popularNow: Flow<CollectionState> = popularSeasonRepository.popularThisSeason.map {
        StateSaver.Home.updatePopularCurrent(it)
    }.stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CollectionState.None
    )

    private val popularNextSeasonRepository by instance<PopularNextSeasonRepository>()
    override val popularNext: Flow<CollectionState> = popularNextSeasonRepository.popularNextSeason.map {
        StateSaver.Home.updatePopularNext(it)
    }.stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CollectionState.None
    )

    private val traceRepository by instance<TraceRepository>()
    override val traceState: Flow<TraceRepository.State> = traceRepository.response

    init {
        traceRepository.clear()
    }

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze,
        ) {
            onRender {
                HomeScreen(this)
            }
        }
    }

    override fun viewProfile() {
        onProfile()
    }

    override fun viewAnime() {
        StateSaver.Home.updateAllLoading()
        launchIO {
            viewTypeExecutor.enqueue {
                appSettings.setViewManga(false)
            }
        }
    }

    override fun viewManga() {
        StateSaver.Home.updateAllLoading()
        launchIO {
            viewTypeExecutor.enqueue {
                appSettings.setViewManga(true)
            }
        }
    }

    override fun viewDiscover() {
        onDiscover()
    }

    override fun viewFavorites() {
        onFavorites()
    }

    override fun details(medium: Medium) {
        onMediumDetails(medium)
    }

    override fun trace(byteArray: ByteArray) {
        traceRepository.search(byteArray)
    }

    override fun clearTrace() {
        traceRepository.clear()
    }
}