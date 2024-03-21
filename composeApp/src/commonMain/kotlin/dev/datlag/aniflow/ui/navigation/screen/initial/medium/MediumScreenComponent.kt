package dev.datlag.aniflow.ui.navigation.screen.initial.medium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.onRenderApplyCommonScheme
import org.kodein.di.DI

class MediumScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val initialMedium: Medium,
    private val onBack: () -> Unit
) : MediumComponent, ComponentContext by componentContext {

    private val backCallback = BackCallback {
        back()
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    override fun render() {
        onRenderApplyCommonScheme(initialMedium.id) {
            MediumScreen(this)
        }
    }

    override fun back() {
        onBack()
    }
}