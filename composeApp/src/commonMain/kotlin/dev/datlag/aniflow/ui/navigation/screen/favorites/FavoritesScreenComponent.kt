package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.ListRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.withMainContext
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance

class FavoritesScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onDiscover: () -> Unit,
    private val onHome: () -> Unit,
    private val onMedium: (Medium) -> Unit
) : FavoritesComponent, ComponentContext by componentContext {

    private val appSettings by instance<Settings.PlatformAppSettings>()
    override val titleLanguage: Flow<TitleLanguage?> = appSettings.titleLanguage

    private val listRepository by instance<ListRepository>()
    override val listState: StateFlow<ListRepository.State> = listRepository.list.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = ListRepository.State.None
    )

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze
        ) {
            onRender {
                FavoritesScreen(this)
            }
        }
    }

    override fun viewDiscover() {
        onDiscover()
    }

    override fun viewHome() {
        onHome()
    }

    override fun details(medium: Medium) {
        onMedium(medium)
    }
}