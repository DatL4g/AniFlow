package dev.datlag.aniflow.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.kmpalette.DominantColorState
import com.kmpalette.palette.graphics.Palette
import com.kmpalette.rememberDominantColorState
import com.kmpalette.rememberPainterDominantColorState
import com.materialkolor.AnimatedDynamicMaterialTheme
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.ktx.isDisliked
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.aniflow.LocalDarkMode
import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.launchIO
import dev.datlag.tooling.compose.withIOContext
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

data object SchemeTheme {

    internal val commonSchemeKey = MutableStateFlow<Any?>(null)
    internal val kache = InMemoryKache<Any, DominantColorState<Painter>>(
        maxSize = 25L * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
    }

    fun setCommon(key: Any?) {
        commonSchemeKey.update { key }
    }
}

@Composable
fun rememberSchemeThemeDominantColor(
    key: Any?,
    state: DominantColorState<Painter>? = null,
): Color? {
    if (key == null) {
        return null
    }

    val fallbackState = remember(state) {
        state
    } ?: remember(key) {
        SchemeTheme.kache.getIfAvailable(key)
    } ?: rememberPainterDominantColorState(
        coroutineContext = ioDispatcher()
    )
    val useState by produceState(fallbackState, key) {
        value = withIOContext {
            SchemeTheme.kache.getOrPut(key) { fallbackState }
        } ?: fallbackState
    }

    return remember(useState) { useState.color }
}

@Composable
fun rememberSchemeThemeDominantColorState(
    key: Any?,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    coroutineContext: CoroutineContext = ioDispatcher(),
    isSwatchValid: (Palette.Swatch) -> Boolean = { true },
    builder: Palette.Builder.() -> Unit = {},
): DominantColorState<Painter> {

    val fallbackState = remember(key) {
        key?.let { SchemeTheme.kache.getIfAvailable(it) }
    } ?: rememberPainterDominantColorState(
        defaultColor = defaultColor,
        defaultOnColor = defaultOnColor,
        coroutineContext = coroutineContext,
        builder = builder,
        isSwatchValid = isSwatchValid
    )
    val state by produceState(fallbackState, key) {
        value = withIOContext {
            key?.let {
                SchemeTheme.kache.getOrPut(it) { fallbackState }
            } ?: fallbackState
        }
    }

    return state
}

@Composable
fun rememberSchemeThemeDominantColorState(
    key: Any?,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    clearFilter: Boolean = false,
    applyMinContrast: Boolean = false,
    minContrastBackgroundColor: Color = Color.Transparent,
    coroutineContext: CoroutineContext = ioDispatcher()
): DominantColorState<Painter> {
    return rememberSchemeThemeDominantColorState(
        key = key,
        defaultColor = defaultColor,
        defaultOnColor = defaultOnColor,
        coroutineContext = coroutineContext,
        builder = {
            if (clearFilter) {
                clearFilters()
            } else {
                addFilter(Palette.DEFAULT_FILTER)
            }
        },
        isSwatchValid = { swatch ->
            if (applyMinContrast) {
                Color(swatch.bodyTextColor).contrastAgainst(minContrastBackgroundColor) >= MinContrastRatio
            } else {
                true
            }
        }
    )
}

val LocalDominantColorState = compositionLocalOf<DominantColorState<Painter>?>{ null }

@Composable
fun SchemeTheme(key: Any?, content: @Composable (DominantColorState<Painter>) -> Unit) {
    val state = rememberSchemeThemeDominantColorState(key)

    DynamicMaterialTheme(
        seedColor = rememberSchemeThemeDominantColor(key, state) ?: MaterialTheme.colorScheme.primary,
        useDarkTheme = LocalDarkMode.current,
        animate = true
    ) {
        CompositionLocalProvider(
            LocalDominantColorState provides state,
        ) {
            content(state)
        }
    }
}

@Composable
fun CommonSchemeTheme(content: @Composable (DominantColorState<Painter>) -> Unit) {
    val key by SchemeTheme.commonSchemeKey.collectAsStateWithLifecycle()

    SchemeTheme(key, content)
}

private fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return maxOf(fgLuminance, bgLuminance) / minOf(fgLuminance, bgLuminance)
}

private const val MinContrastRatio = 3f