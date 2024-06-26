package dev.datlag.aniflow.ui.navigation.screen.nekos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.nekos.NekosRepository
import dev.datlag.aniflow.nekos.model.Rating
import dev.datlag.aniflow.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.kodein.di.DI
import org.kodein.di.instance

class NekosScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onBack: () -> Unit,
) : NekosComponent, ComponentContext by componentContext {

    private val appSettings by instance<Settings.PlatformAppSettings>()
    override val adultContent: Flow<Boolean> = appSettings.adultContent

    private val nekosRepository by instance<NekosRepository>()
    override val rating: StateFlow<Rating> = nekosRepository.rating
    override val state: Flow<NekosRepository.State> = nekosRepository.response

    @Composable
    override fun render() {
        val haze = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides haze
        ) {
            onRender {
                NekosScreen(this)
            }
        }
    }

    override fun back() {
        onBack()
    }

    override fun filter(rating: Rating) {
        nekosRepository.rating(rating)
    }
}