package dev.datlag.aniflow.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimator
import com.arkivanov.decompose.router.stack.*
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.ifValueOrNull
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.ui.navigation.screen.favorites.FavoritesScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.home.HomeScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.settings.SettingsScreen
import dev.datlag.aniflow.ui.navigation.screen.settings.SettingsScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.wallpaper.WallpaperScreenComponent
import io.github.aakira.napier.Napier
import org.kodein.di.DI
import org.kodein.di.instance

class RootComponent(
    componentContext: ComponentContext,
    override val di: DI
) : Component, ComponentContext by componentContext {

    private val userHelper by instance<UserHelper>()
    private val navigation = StackNavigation<RootConfig>()
    private val stack = childStack(
        source = navigation,
        serializer = RootConfig.serializer(),
        initialConfiguration = RootConfig.Home,
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        rootConfig: RootConfig,
        componentContext: ComponentContext
    ): Component {
        return when (rootConfig) {
            is RootConfig.Home -> HomeScreenComponent(
                componentContext = componentContext,
                di = di,
                onMediumDetails = {
                    navigation.push(RootConfig.Details(it))
                },
                onProfile = {
                    navigation.push(RootConfig.Settings)
                },
                onWallpaper = {
                    navigation.replaceCurrent(RootConfig.Wallpaper)
                },
                onFavorites = {
                    navigation.replaceCurrent(RootConfig.Favorites)
                }
            )
            is RootConfig.Details -> MediumScreenComponent(
                componentContext = componentContext,
                di = di,
                initialMedium = rootConfig.medium,
                onBack = navigation::pop
            )
            is RootConfig.Settings -> SettingsScreenComponent(
                componentContext = componentContext,
                di = di
            )
            is RootConfig.Favorites -> FavoritesScreenComponent(
                componentContext = componentContext,
                di = di,
                onWallpaper = {
                    navigation.replaceCurrent(RootConfig.Wallpaper)
                },
                onHome = {
                    navigation.replaceCurrent(RootConfig.Home)
                }
            )
            is RootConfig.Wallpaper -> WallpaperScreenComponent(
                componentContext = componentContext,
                di = di,
                onHome = {
                    navigation.replaceCurrent(RootConfig.Home)
                },
                onFavorites = {
                    navigation.replaceCurrent(RootConfig.Favorites)
                }
            )
        }
    }

    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    override fun render() {
        onRender {
            Children(
                stack = stack,
                animation = predictiveBackAnimation(
                    backHandler = this.backHandler,
                    fallbackAnimation = stackAnimation(fade()),
                    onBack = {
                        navigation.pop()
                    }
                )
            ) {
                it.instance.render()
            }
        }
    }

    fun onDeepLink(mediumId: Int) {
        navigation.replaceAll(RootConfig.Home, RootConfig.Details(mediumId))
    }

    fun onLogin(accessToken: String, expiresIn: Int?) {
        launchIO {
            userHelper.saveLogin(accessToken, expiresIn)
        }
    }
}