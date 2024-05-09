package dev.datlag.aniflow.ui.navigation.screen.settings

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
    override val di: DI,
    private val onNekos: () -> Unit
) : SettingsComponent, ComponentContext by componentContext {

    private val appSettings by di.instance<Settings.PlatformAppSettings>()
    private val userHelper by di.instance<UserHelper>()

    override val user: Flow<User?> = userHelper.user.flowOn(ioDispatcher())
    override val isLoggedIn: Flow<Boolean> = userHelper.isLoggedIn.flowOn(ioDispatcher())
    override val loginUri: String = userHelper.loginUrl

    override val adultContent: Flow<Boolean> = appSettings.adultContent.flowOn(ioDispatcher())
    override val selectedColor: Flow<SettingsColor?> = appSettings.color.flowOn(ioDispatcher())
    override val selectedTitleLanguage: Flow<SettingsTitle?> = appSettings.titleLanguage.flowOn(ioDispatcher())
    override val selectedCharLanguage: Flow<SettingsChar?> = appSettings.charLanguage.flowOn(ioDispatcher())

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

    override fun logout() {
        launchIO {
            userHelper.logout()
        }
    }

    override fun nekos() {
        onNekos()
    }
}