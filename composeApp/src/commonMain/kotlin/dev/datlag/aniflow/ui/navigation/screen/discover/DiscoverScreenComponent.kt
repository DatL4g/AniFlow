package dev.datlag.aniflow.ui.navigation.screen.discover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.SearchRepository
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.CollectionState
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.tooling.compose.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
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

    private val searchRepository by instance<SearchRepository>()

    override val initialSearchValue: String?
        get() = searchRepository.searchQuery

    override val type: Flow<MediaType> = searchRepository.type

    override val searchResult: Flow<CollectionState> = searchRepository.result.flowOn(
        context = ioDispatcher()
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
        searchRepository.query(query)
    }

    override fun toggleView() {
        searchRepository.toggleType()
    }
}