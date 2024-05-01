package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
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
    private val userHelper by di.instance<UserHelper>()

    override val user: Flow<User?> = userHelper.user
    override val adultContent: Flow<Boolean> = appSettings.adultContent
    override val selectedColor: Flow<AppSettings.Color?> = appSettings.color
    override val selectedTitleLanguage: Flow<AppSettings.TitleLanguage?> = appSettings.titleLanguage

    @Composable
    override fun render() {
        onRender {
            SettingsScreen(this)
        }
    }

    override fun changeAdultContent(value: Boolean) {
        launchDefault {
            userHelper.updateAdultSetting(value)
        }
    }

    override fun changeProfileColor(value: AppSettings.Color?) {
        launchDefault {
            userHelper.updateProfileColorSetting(value)
        }
    }

    override fun changeTitleLanguage(value: AppSettings.TitleLanguage?) {
        launchDefault {
            userHelper.updateTitleLanguage(value)
        }
    }
}