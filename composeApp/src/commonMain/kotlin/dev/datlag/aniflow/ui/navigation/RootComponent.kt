package dev.datlag.aniflow.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumScreen
import dev.datlag.aniflow.ui.navigation.screen.medium.MediumScreenComponent
import org.kodein.di.DI

class RootComponent(
    componentContext: ComponentContext,
    override val di: DI
) : Component, ComponentContext by componentContext {

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
            is RootConfig.Home -> InitialScreenComponent(
                componentContext = componentContext,
                di = di,
                onMediumDetails = {
                    navigation.push(RootConfig.Details(it))
                }
            )
            is RootConfig.Details -> MediumScreenComponent(
                componentContext = componentContext,
                di = di,
                initialMedium = rootConfig.medium,
                onBack = navigation::pop
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
                    onBack = {
                        navigation.pop()
                    }
                )
            ) {
                it.instance.render()
            }
        }
    }
}