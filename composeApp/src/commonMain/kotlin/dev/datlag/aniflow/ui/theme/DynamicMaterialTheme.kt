package dev.datlag.aniflow.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.Contrast
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import dev.datlag.aniflow.LocalDarkMode

@Composable
fun DynamicMaterialTheme(
    seedColor: Color?,
    content: @Composable () -> Unit
) {

    val dynamicColorScheme = if (seedColor != null) {
        rememberDynamicColorScheme(
            seedColor = seedColor,
            isDark = LocalDarkMode.current,
            style = PaletteStyle.TonalSpot,
            contrastLevel = Contrast.Default.value,
            isExtendedFidelity = false
        )
    } else {
        MaterialTheme.colorScheme
    }
    val colorScheme = dynamicColorScheme.copy(
        primary = dynamicColorScheme.primary,
        primaryContainer = dynamicColorScheme.primaryContainer,
        secondary = dynamicColorScheme.secondary,
        secondaryContainer = dynamicColorScheme.secondaryContainer,
        tertiary = dynamicColorScheme.tertiary,
        tertiaryContainer = dynamicColorScheme.tertiaryContainer,
        background = dynamicColorScheme.background,
        surface = dynamicColorScheme.surface,
        surfaceTint = dynamicColorScheme.surfaceTint,
        surfaceBright = dynamicColorScheme.surfaceBright,
        surfaceDim = dynamicColorScheme.surfaceDim,
        surfaceContainer = dynamicColorScheme.surfaceContainer,
        surfaceContainerHigh = dynamicColorScheme.surfaceContainerHigh,
        surfaceContainerHighest = dynamicColorScheme.surfaceContainerHighest,
        surfaceContainerLow = dynamicColorScheme.surfaceContainerLow,
        surfaceContainerLowest = dynamicColorScheme.surfaceContainerLowest,
        surfaceVariant = dynamicColorScheme.surfaceVariant,
        error = dynamicColorScheme.error,
        errorContainer = dynamicColorScheme.errorContainer,
        onPrimary = dynamicColorScheme.onPrimary,
        onPrimaryContainer = dynamicColorScheme.onPrimaryContainer,
        onSecondary = dynamicColorScheme.onSecondary,
        onSecondaryContainer = dynamicColorScheme.onSecondaryContainer,
        onTertiary = dynamicColorScheme.onTertiary,
        onTertiaryContainer = dynamicColorScheme.onTertiaryContainer,
        onBackground = dynamicColorScheme.onBackground,
        onSurface = dynamicColorScheme.onSurface,
        onSurfaceVariant = dynamicColorScheme.onSurfaceVariant,
        onError = dynamicColorScheme.onError,
        onErrorContainer = dynamicColorScheme.onErrorContainer,
        inversePrimary = dynamicColorScheme.inversePrimary,
        inverseSurface = dynamicColorScheme.inverseSurface,
        inverseOnSurface = dynamicColorScheme.inverseOnSurface,
        outline = dynamicColorScheme.outline,
        outlineVariant = dynamicColorScheme.outlineVariant,
        scrim = dynamicColorScheme.scrim,
    )

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        content()
    }
}

@Composable
private fun Color.animate(animationSpec: AnimationSpec<Color>): Color {
    return animateColorAsState(this, animationSpec).value
}