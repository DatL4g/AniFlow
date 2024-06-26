package dev.datlag.aniflow.ui.navigation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularNextSeasonStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.state.HomeAiringState
import dev.datlag.aniflow.anilist.state.HomeDefaultState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.coroutines.Executor
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.aniflow.trace.TraceRepository
import dev.datlag.aniflow.ui.navigation.DialogComponent
import dev.datlag.aniflow.ui.navigation.screen.home.dialog.about.AboutDialogComponent
import dev.datlag.aniflow.ui.navigation.screen.home.dialog.settings.SettingsDialogComponent
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onMediumDetails: (Medium) -> Unit,
    private val onDiscover: () -> Unit,
    private val onFavorites: () -> Unit,
    private val onNekos: () -> Unit
) : HomeComponent, ComponentContext by componentContext {

    private val appSettings by instance<Settings.PlatformAppSettings>()
    override val viewing = appSettings.viewManga.map {
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
        }
    }
    override val titleLanguage: Flow<TitleLanguage?> = appSettings.titleLanguage

    private val viewTypeExecutor = Executor()

    private val userHelper by instance<UserHelper>()
    override val user: Flow<User?> = userHelper.user
    override val loggedIn: Flow<Boolean> = userHelper.isLoggedIn

    private val stateScope = ioScope()
    private val airingTodayRepository by instance<AiringTodayStateMachine>()
    override val airing: StateFlow<HomeAiringState> = airingTodayRepository.airing.map {
        StateSaver.Home.updateAiring(it)
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = airingTodayRepository.currentState
    )

    private val trendingRepository by instance<TrendingStateMachine>()
    override val trending: StateFlow<HomeDefaultState> = trendingRepository.trending.map {
        StateSaver.Home.updateTrending(it)
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = trendingRepository.currentState
    )

    private val popularSeasonRepository by instance<PopularSeasonStateMachine>()
    override val popularNow: StateFlow<HomeDefaultState> = popularSeasonRepository.popular.map {
        StateSaver.Home.updatePopularCurrent(it)
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = popularSeasonRepository.currentState
    )

    private val popularNextSeasonRepository by instance<PopularNextSeasonStateMachine>()
    override val popularNext: StateFlow<HomeDefaultState> = popularNextSeasonRepository.popularNext.map {
        StateSaver.Home.updatePopularNext(it)
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = stateScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = popularNextSeasonRepository.currentState
    )

    private val traceRepository by instance<TraceRepository>()
    override val traceState: Flow<TraceRepository.State> = traceRepository.response.flowOn(context = ioDispatcher())

    private val dialogNavigation = SlotNavigation<DialogConfig>()
    override val dialog: Value<ChildSlot<DialogConfig, DialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer()
    ) { config, context ->
        when (config) {
            is DialogConfig.Settings -> SettingsDialogComponent(
                componentContext = context,
                di = di,
                onNekos = onNekos,
                onDismiss = dialogNavigation::dismiss,
                onAbout = {
                    dialogNavigation.activate(DialogConfig.About)
                }
            )
            is DialogConfig.About -> AboutDialogComponent(
                componentContext = context,
                di = di,
                onDismiss = dialogNavigation::dismiss
            )
        }
    }

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
        dialogNavigation.activate(DialogConfig.Settings)
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