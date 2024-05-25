package dev.datlag.aniflow.ui.navigation.screen.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.EditMutation
import dev.datlag.aniflow.anilist.ListStateMachine
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.state.ListState
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.coroutines.Executor
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.TitleLanguage
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.withIOContext
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

    private val apolloClient by instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT)
    private val listRepository by instance<ListStateMachine>()
    override val listState: StateFlow<ListState> = listRepository.list.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = listRepository.currentState
    )

    private val increaseExecutor = Executor()
    override val type: Flow<MediaType> = listRepository.type
    override val status: Flow<MediaListStatus> = listRepository.status

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

    override fun increase(medium: Medium, progress: Int) {
        val newStatus = if (progress >= medium.episodesOrChapters) {
            when (val current = medium.entry?.status) {
                MediaListStatus.REPEATING -> current
                else -> MediaListStatus.COMPLETED
            }
        } else {
            medium.entry?.status ?: MediaListStatus.UNKNOWN__
        }

        val mutation = EditMutation(
            mediaId = Optional.present(medium.id),
            progress = Optional.present(progress),
            status = if (newStatus == MediaListStatus.UNKNOWN__) {
                Optional.absent()
            } else {
                Optional.present(newStatus)
            },
            repeat = Optional.absent()
        )

        launchIO {
            increaseExecutor.enqueue {
                apolloClient.mutation(mutation).execute()
            }
        }
    }

    override fun toggleView() {
        listRepository.toggleType()
    }

    override fun setStatus(status: MediaListStatus) {
        listRepository.status(status)
    }

    override suspend fun nextPage() {
        listRepository.nextPage()
    }
}