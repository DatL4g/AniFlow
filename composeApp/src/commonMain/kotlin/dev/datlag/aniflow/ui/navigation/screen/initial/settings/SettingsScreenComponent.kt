package dev.datlag.aniflow.ui.navigation.screen.initial.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance
import dev.datlag.aniflow.settings.model.Color as SettingsColor
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar

class SettingsScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : SettingsComponent, ComponentContext by componentContext {

    private val appSettings by di.instance<Settings.PlatformAppSettings>()
    private val userHelper by di.instance<UserHelper>()

    override val user: Flow<User?> = userHelper.user
    override val adultContent: Flow<Boolean> = appSettings.adultContent
    override val selectedColor: Flow<SettingsColor?> = appSettings.color
    override val selectedTitleLanguage: Flow<SettingsTitle?> = appSettings.titleLanguage
    override val selectedCharLanguage: Flow<SettingsChar?> = appSettings.charLanguage

    @Composable
    override fun render() {
        onRender {
            SettingsScreen(this)
        }
    }

    override fun changeAdultContent(value: Boolean) {
        launchIO {
            userHelper.updateAdultSetting(value)
        }
    }

    override fun changeProfileColor(value: SettingsColor?) {
        launchIO {
            userHelper.updateProfileColorSetting(value)
        }
    }

    override fun changeTitleLanguage(value: SettingsTitle?) {
        launchIO {
            userHelper.updateTitleLanguage(value)
        }
    }

    override fun changeCharLanguage(value: SettingsChar?) {
        launchIO {
            userHelper.updateCharLanguage(value)
        }
    }
}