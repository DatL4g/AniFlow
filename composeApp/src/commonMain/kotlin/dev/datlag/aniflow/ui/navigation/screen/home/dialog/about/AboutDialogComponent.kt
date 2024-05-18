package dev.datlag.aniflow.ui.navigation.screen.home.dialog.about

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.common.onRender
import org.kodein.di.DI

class AboutDialogComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onDismiss: () -> Unit
) : AboutComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        onRender {
            AboutDialog(this)
        }
    }

    override fun dismiss() {
        onDismiss()
    }
}