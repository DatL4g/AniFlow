package dev.datlag.aniflow.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.platform.LocalLayoutDirection

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
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.2f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1500.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        )
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation - 500, y = 0.0f),
        end = Offset(x = translateAnimation, y = 270F),
    )
}

fun Modifier.shimmer(shape: Shape = RectangleShape): Modifier = composed {
    this.background(
        brush = shimmerBrush(),
        shape = shape
    )
}

@Composable
fun shimmerPainter(): BrushPainter {
    return BrushPainter(shimmerBrush())
}
