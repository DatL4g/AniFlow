package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.other.BurningSeriesResolver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.kodein.di.DI
import org.kodein.di.instance

class EditDialogComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val titleFlow: Flow<Medium.Title>,
    private val onDismiss: () -> Unit
) : EditComponent, ComponentContext by componentContext {

    private val burningSeriesResolver by instance<BurningSeriesResolver>()

    override val bsAvailable: Boolean
        get() = burningSeriesResolver.isAvailable

    @OptIn(ExperimentalCoroutinesApi::class)
    override val bsOptions = titleFlow.mapLatest {
        burningSeriesResolver.resolveByName(it.english, it.romaji)
    }

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