package dev.datlag.aniflow.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import dev.datlag.aniflow.LocalDI
import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.SeasonState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.coroutines.Executor
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.trace.TraceStateMachine
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumScreenComponent
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.time.Duration.Companion.seconds
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onMediumDetails: (Medium) -> Unit
) : HomeComponent, ComponentContext by componentContext {

    private val appSettings by di.instance<Settings.PlatformAppSettings>()
    override val titleLanguage: Flow<SettingsTitle?> = appSettings.titleLanguage.flowOn(ioDispatcher())

    private val airingTodayRepository by di.instance<AiringTodayRepository>()
    override val airingState: Flow<AiringTodayRepository.State> = airingTodayRepository.airing.map {
        StateSaver.Home.updateAiring(it)
    }

    private val trendingRepository by di.instance<TrendingRepository>()
    override val trendingState: Flow<TrendingRepository.State> = trendingRepository.trending.map {
        StateSaver.Home.updateTrending(it)
    }

    private val popularSeasonRepository by di.instance<PopularSeasonRepository>()
    override val popularSeasonState: Flow<SeasonState> = popularSeasonRepository.popularThisSeason.map {
        StateSaver.Home.updatePopularCurrent(it)
    }

    private val popularNextSeasonRepository by di.instance<PopularNextSeasonRepository>()
    override val popularNextSeasonState: Flow<SeasonState> = popularNextSeasonRepository.popularNextSeason.map {
        StateSaver.Home.updatePopularNext(it)
    }

    private val traceStateMachine by di.instance<TraceStateMachine>()
    override val traceState: Flow<TraceStateMachine.State> = traceStateMachine.state.flowOn(
        context = ioDispatcher()
    )

    private val viewTypeExecutor = Executor()

    override val viewing = appSettings.viewManga.map {
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
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
        onMediumDetails(medium)
    }

    override fun trace(channel: ByteArray) {
        launchIO {
            traceStateMachine.dispatch(TraceStateMachine.Action.Load(channel))
        }
    }

    override fun viewAnime() {
        launchIO {
            viewTypeExecutor.enqueue {
                appSettings.setViewManga(false)
            }
        }
    }

    override fun viewManga() {
        launchIO {
            viewTypeExecutor.enqueue {
                appSettings.setViewManga(true)
            }
        }
    }
}