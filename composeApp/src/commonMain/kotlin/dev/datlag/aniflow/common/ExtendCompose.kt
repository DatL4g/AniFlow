package dev.datlag.aniflow.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.kmpalette.DominantColorState
import dev.datlag.aniflow.LocalPaddingValues

fun Modifier.bottomShadowBrush(color: Color, alpha: Float = 1F): Modifier {
    val maxAlpha = kotlin.math.min(alpha, 1F)

    return this.background(
        brush = Brush.verticalGradient(
            0.0f to Color.Transparent,
            0.1f to color.copy(alpha = 0.35f * maxAlpha),
            0.3f to color.copy(alpha = 0.55f * maxAlpha),
            0.5f to color.copy(alpha = 0.75f * maxAlpha),
            0.7f to color.copy(alpha = 0.95f * maxAlpha),
            0.9f to color.copy(alpha = 1f * maxAlpha)
        )
    )
}

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(direction) + other.calculateStartPadding(direction),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(direction) + other.calculateEndPadding(direction),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

@Composable
fun PaddingValues.merge(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = max(this.calculateStartPadding(direction), other.calculateStartPadding(direction)),
        top = max(this.calculateTopPadding(), other.calculateTopPadding()),
        end = max(this.calculateEndPadding(direction), other.calculateEndPadding(direction)),
        bottom = max(this.calculateBottomPadding(), other.calculateBottomPadding())
    )
}

fun Modifier.localPadding(additional: PaddingValues = PaddingValues(0.dp)) = composed {
    this.padding(LocalPaddingValues.current?.plus(additional) ?: additional)
}

fun Modifier.localPadding(all: Dp) = composed {
    this.localPadding(PaddingValues(all))
}

fun Modifier.localPadding(horizontal: Dp, vertical: Dp = 0.dp) = composed {
    this.localPadding(PaddingValues(horizontal = horizontal, vertical = vertical))
}

@Composable
fun LocalPadding(additional: PaddingValues = PaddingValues(0.dp)): PaddingValues {
    return LocalPaddingValues.current?.plus(additional) ?: additional
}

@Composable
fun LocalPadding(additional: Dp): PaddingValues {
    return LocalPaddingValues.current?.plus(PaddingValues(additional)) ?: PaddingValues(additional)
}

@Composable
fun LocalPadding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues {
    return LocalPaddingValues.current?.plus(
        PaddingValues(horizontal = horizontal, vertical = vertical)
    ) ?: PaddingValues(horizontal = horizontal, vertical = vertical)
}

@Composable
fun LocalPadding(top: Dp = 0.dp, start: Dp = 0.dp, bottom: Dp = 0.dp, end: Dp = 0.dp): PaddingValues {
    return LocalPaddingValues.current?.plus(
        PaddingValues(top = top, start = start, bottom = bottom, end = end)
    ) ?: PaddingValues(top = top, start = start, bottom = bottom, end = end)
}

fun Modifier.mergedLocalPadding(other: PaddingValues, additional: PaddingValues = PaddingValues(0.dp)) = composed {
    this.padding((LocalPaddingValues.current?.merge(other) ?: other).plus(additional))
}

fun Modifier.mergedLocalPadding(other: PaddingValues, additional: Dp) = composed {
    this.mergedLocalPadding(other, PaddingValues(additional))
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) {
        mutableStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember(this) {
        mutableStateOf(firstVisibleItemScrollOffset)
    }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

/**
 * Checks if the modal is currently expanded or a swipe action is in progress to be expanded.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.isFullyExpandedOrTargeted(forceFullExpand: Boolean = false): Boolean {
    val checkState = if (this.hasExpandedState) {
        if (!this.hasPartiallyExpandedState) {
            return false
        }
        SheetValue.Expanded
    } else {
        if (forceFullExpand) {
            return false
        }
        SheetValue.PartiallyExpanded
    }

    return this.targetValue == checkState
}

val <T : Any> DominantColorState<T>?.primary
    @Composable
    get() = this?.color ?: MaterialTheme.colorScheme.primary

val <T : Any> DominantColorState<T>?.onPrimary
    @Composable
    get() = this?.onColor ?: MaterialTheme.colorScheme.onPrimary

val Color.plainOnColor: Color
    get() = if (this.luminance() > 0.5F) {
        Color.Black
    } else {
        Color.White
    }