package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.settings.Settings
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance

class SettingsScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : SettingsComponent, ComponentContext by componentContext {

    private val appSettings by di.instance<Settings.PlatformAppSettings>()
    override val adultContent: Flow<Boolean> = appSettings.adultContent.flowOn(ioDispatcher())

    @Composable
    override fun render() {
        onRender {
            SettingsScreen(this)
        }
    }

    override fun changeAdultContent(value: Boolean) {
        launchIO {
            appSettings.setAdultContent(value)
        }
    }
}