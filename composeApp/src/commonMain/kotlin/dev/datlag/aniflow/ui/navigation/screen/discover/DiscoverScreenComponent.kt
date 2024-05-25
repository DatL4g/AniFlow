package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.DiscoverStateMachine
import dev.datlag.aniflow.anilist.SearchStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.DiscoverState
import dev.datlag.aniflow.anilist.state.SearchState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.kodein.di.DI
import org.kodein.di.instance

class DiscoverScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onHome: () -> Unit,
    private val onList: () -> Unit,
    private val onMedium: (Medium) -> Unit
) : DiscoverComponent, ComponentContext by componentContext {

    private val userHelper by instance<UserHelper>()
    override val loggedIn: Flow<Boolean> = userHelper.isLoggedIn

    private val searchRepository by instance<SearchStateMachine>()

    override val initialSearchValue: String?
        get() = searchRepository.searchQuery

    override val type: Flow<MediaType> = searchRepository.type

    override val searchResult: StateFlow<SearchState> = searchRepository.result.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = searchRepository.currentState
    )

    private val discoverStateMachine by instance<DiscoverStateMachine>()
    override val state: StateFlow<DiscoverState> = discoverStateMachine.state.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = discoverStateMachine.currentState
    )

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze
        ) {
            onRender {
                DiscoverScreen(this)
            }
        }
    }

    override fun viewHome() {
        onHome()
    }

    override fun viewList() {
        onList()
    }

    override fun details(medium: Medium) {
        onMedium(medium)
    }

    override fun search(query: String) {
        searchRepository.search(query)
    }

    override fun toggleView() {
        searchRepository.toggleType()
    }
}