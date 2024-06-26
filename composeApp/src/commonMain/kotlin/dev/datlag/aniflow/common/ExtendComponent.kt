package dev.datlag.aniflow.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.kmpalette.DominantColorState
import dev.datlag.aniflow.LocalDI
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.theme.SchemeTheme
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

/**
 * Can be placed in the Component interface again when
 * [https://github.com/JetBrains/compose-multiplatform/issues/3205](https://github.com/JetBrains/compose-multiplatform/issues/3205)
 * is fixed
 */
@Composable
fun Component.onRender(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalDI provides di,
        LocalLifecycleOwner provides object : LifecycleOwner {
            override val lifecycle: Lifecycle = this@onRender.lifecycle
        }
    ) {
        content()
    }
    SideEffect {
        nullableFirebaseInstance()?.crashlytics?.screen(this)

        val bs by instanceOrNull<BurningSeriesResolver>()
        bs?.let {
            nullableFirebaseInstance()?.crashlytics?.bs(it)
        }
    }
}

@Composable
fun Component.onRenderWithScheme(key: Any?, content: @Composable (SchemeTheme.Updater?) -> Unit) {
    onRender {
        SchemeTheme(key = key, content = content)
    }
}

@Composable
fun <T, R> StateFlow<T>.mapCollect(transform: (value: T) -> R): State<R> {
    return remember(this) {
        this.map(transform)
    }.collectAsStateWithLifecycle(
        initialValue = transform(this.value)
    )
}

@Composable
fun <T, R> Flow<T>.mapCollect(defaultValue: T, transform: (value: T) -> R): State<R> {
    return remember(this) {
        this.map(transform)
    }.collectAsStateWithLifecycle(
        initialValue = transform(defaultValue)
    )
}