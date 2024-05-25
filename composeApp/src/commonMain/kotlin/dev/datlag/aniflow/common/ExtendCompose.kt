package dev.datlag.aniflow.common

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.kmpalette.DominantColorState

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

val LazyListState.canScroll: Boolean
    get() = this.canScrollForward || this.canScrollBackward

@Composable
fun LazyListState.scrollUpVisible(): Boolean {
    return if (canScroll) {
        isScrollingUp() && canScrollForward
    } else {
        true
    }
}

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
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

val LazyGridState.canScroll: Boolean
    get() = this.canScrollForward || this.canScrollBackward

@Composable
fun LazyGridState.scrollUpVisible(): Boolean {
    return if (canScroll) {
        isScrollingUp() && canScrollForward
    } else {
        true
    }
}

@Composable
fun LazyStaggeredGridState.isScrollingUp(): Boolean {
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

val LazyStaggeredGridState.canScroll: Boolean
    get() = this.canScrollForward || this.canScrollBackward

@Composable
fun LazyStaggeredGridState.scrollUpVisible(): Boolean {
    return if (canScroll) {
        isScrollingUp() && canScrollForward
    } else {
        true
    }
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

@Composable
fun animatePaddingAsState(
    targetValues: PaddingValues,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    animationSpec: AnimationSpec<PaddingValues> = spring(),
    label: String = "PaddingAnimation",
    finishedListener: ((PaddingValues) -> Unit)? = null
): State<PaddingValues> {
    return animateValueAsState(
        targetValue = targetValues,
        typeConverter = PaddingToVector(layoutDirection),
        animationSpec = animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}

private fun PaddingToVector(direction: LayoutDirection): TwoWayConverter<PaddingValues, AnimationVector4D> = TwoWayConverter(
    convertToVector = {
        AnimationVector4D(
            v1 = it.calculateTopPadding().value,
            v2 = it.calculateStartPadding(direction).value,
            v3 = it.calculateEndPadding(direction).value,
            v4 = it.calculateBottomPadding().value
        )
    },
    convertFromVector = {
        PaddingValues(
            top = it.v1.dp,
            start = it.v2.dp,
            end = it.v3.dp,
            bottom = it.v4.dp
        )
    }
)

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}