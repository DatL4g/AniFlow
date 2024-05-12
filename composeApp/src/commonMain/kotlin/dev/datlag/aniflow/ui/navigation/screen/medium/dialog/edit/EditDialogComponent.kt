package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.other.Series
import dev.datlag.tooling.compose.ioDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import org.kodein.di.DI
import org.kodein.di.instance

class EditDialogComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onDismiss: () -> Unit
) : EditComponent, ComponentContext by componentContext {



    @Composable
    override fun render() {
        onRender {
            EditDialog(this)
        }
    }

    override fun dismiss() {
        onDismiss()
    }
}