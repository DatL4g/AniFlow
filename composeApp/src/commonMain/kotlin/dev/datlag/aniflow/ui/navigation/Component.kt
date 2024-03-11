package dev.datlag.aniflow.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.tooling.compose.launchDefault
import dev.datlag.tooling.compose.launchIO
import dev.datlag.tooling.compose.launchMain
import dev.datlag.tooling.decompose.defaultScope
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.decompose.mainScope
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DIAware

interface Component : DIAware, ComponentContext {

    @Composable
    fun render()

    fun launchIO(block: suspend CoroutineScope.() -> Unit) = ioScope().launchIO(block)
    fun launchMain(block: suspend CoroutineScope.() -> Unit) = mainScope().launchMain(block)
    fun launchDefault(block: suspend CoroutineScope.() -> Unit) = defaultScope().launchDefault(block)
}