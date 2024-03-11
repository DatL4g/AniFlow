package dev.datlag.aniflow.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data object Colors {

    private const val THEME_LIGHT_PRIMARY = 0xFF585992
    private const val THEME_LIGHT_ON_PRIMARY = 0xFFFFFFFF
    private const val THEME_LIGHT_PRIMARY_CONTAINER = 0xFFE1DFFF
    private const val THEME_LIGHT_ON_PRIMARY_CONTAINER = 0xFF14134A

    private const val THEME_DARK_PRIMARY = 0xFFC1C1FF
    private const val THEME_DARK_ON_PRIMARY = 0xFF292A60
    private const val THEME_DARK_PRIMARY_CONTAINER = 0xFF404178
    private const val THEME_DARK_ON_PRIMARY_CONTAINER = 0xFFE1DFFF

    private const val THEME_LIGHT_SECONDARY = 0xFF585992
    private const val THEME_LIGHT_ON_SECONDARY = 0xFFFFFFFF
    private const val THEME_LIGHT_SECONDARY_CONTAINER = 0xFFE1DFFF
    private const val THEME_LIGHT_ON_SECONDARY_CONTAINER = 0xFF14134A

    private const val THEME_DARK_SECONDARY = 0xFFC1C1FF
    private const val THEME_DARK_ON_SECONDARY = 0xFF2A2A60
    private const val THEME_DARK_SECONDARY_CONTAINER = 0xFF404178
    private const val THEME_DARK_ON_SECONDARY_CONTAINER = 0xFFE1DFFF

    private const val THEME_LIGHT_TERTIARY = 0xFF864B6E
    private const val THEME_LIGHT_ON_TERTIARY = 0xFFFFFFFF
    private const val THEME_LIGHT_TERTIARY_CONTAINER = 0xFFFFD8EA
    private const val THEME_LIGHT_ON_TERTIARY_CONTAINER = 0xFF370728

    private const val THEME_DARK_TERTIARY = 0xFFFBB1D8
    private const val THEME_DARK_ON_TERTIARY = 0xFF511D3E
    private const val THEME_DARK_TERTIARY_CONTAINER = 0xFF6B3455
    private const val THEME_DARK_ON_TERTIARY_CONTAINER = 0xFFFFD8EA

    private const val THEME_LIGHT_ERROR = 0xFFBA1A1A
    private const val THEME_LIGHT_ON_ERROR = 0xFFFFFFFF
    private const val THEME_LIGHT_ERROR_CONTAINER = 0xFFFFDAD6
    private const val THEME_LIGHT_ON_ERROR_CONTAINER = 0xFF410002

    private const val THEME_DARK_ERROR = 0xFFFFB4AB
    private const val THEME_DARK_ON_ERROR = 0xFF690005
    private const val THEME_DARK_ERROR_CONTAINER = 0xFF93000A
    private const val THEME_DARK_ON_ERROR_CONTAINER = 0xFFFFDAD6

    private const val THEME_LIGHT_BACKGROUND = 0xFFFCF8FF
    private const val THEME_LIGHT_ON_BACKGROUND = 0xFF1B1B21

    private const val THEME_DARK_BACKGROUND = 0xFF131318
    private const val THEME_DARK_ON_BACKGROUND = 0xFFE4E1E9

    private const val THEME_LIGHT_SURFACE = 0xFFFCF8FF
    private const val THEME_LIGHT_ON_SURFACE = 0xFF1B1B21
    private const val THEME_LIGHT_SURFACE_VARIANT = 0xFFE4E1EC
    private const val THEME_LIGHT_ON_SURFACE_VARIANT = 0xFF47464F

    private const val THEME_DARK_SURFACE = 0xFF131318
    private const val THEME_DARK_ON_SURFACE = 0xFFE4E1E9
    private const val THEME_DARK_SURFACE_VARIANT = 0xFF47464F
    private const val THEME_DARK_ON_SURFACE_VARIANT = 0xFFC8C5D0

    private const val THEME_LIGHT_OUTLINE = 0xFF777680
    private const val THEME_LIGHT_INVERSE_SURFACE = 0xFF303036
    private const val THEME_LIGHT_INVERSE_ON_SURFACE = 0xFFF3EFF7
    private const val THEME_LIGHT_INVERSE_PRIMARY = 0xFFC1C1FF

    private const val THEME_DARK_OUTLINE = 0xFF918F9A
    private const val THEME_DARK_INVERSE_SURFACE = 0xFFE4E1E9
    private const val THEME_DARK_INVERSE_ON_SURFACE = 0xFF303036
    private const val THEME_DARK_INVERSE_PRIMARY = 0xFF585992

    fun getDarkScheme() = darkColorScheme(
        primary = Color(THEME_DARK_PRIMARY),
        onPrimary = Color(THEME_DARK_ON_PRIMARY),
        primaryContainer = Color(THEME_DARK_PRIMARY_CONTAINER),
        onPrimaryContainer = Color(THEME_DARK_ON_PRIMARY_CONTAINER),

        secondary = Color(THEME_DARK_SECONDARY),
        onSecondary = Color(THEME_DARK_ON_SECONDARY),
        secondaryContainer = Color(THEME_DARK_SECONDARY_CONTAINER),
        onSecondaryContainer = Color(THEME_DARK_ON_SECONDARY_CONTAINER),

        tertiary = Color(THEME_DARK_TERTIARY),
        onTertiary = Color(THEME_DARK_ON_TERTIARY),
        tertiaryContainer = Color(THEME_DARK_TERTIARY_CONTAINER),
        onTertiaryContainer = Color(THEME_DARK_ON_TERTIARY_CONTAINER),

        error = Color(THEME_DARK_ERROR),
        errorContainer = Color(THEME_DARK_ERROR_CONTAINER),
        onError = Color(THEME_DARK_ON_ERROR),
        onErrorContainer = Color(THEME_DARK_ON_ERROR_CONTAINER),

        background = Color(THEME_DARK_BACKGROUND),
        onBackground = Color(THEME_DARK_ON_BACKGROUND),

        surface = Color(THEME_DARK_SURFACE),
        onSurface = Color(THEME_DARK_ON_SURFACE),
        surfaceVariant = Color(THEME_DARK_SURFACE_VARIANT),
        onSurfaceVariant = Color(THEME_DARK_ON_SURFACE_VARIANT),

        outline = Color(THEME_DARK_OUTLINE),
        inverseSurface = Color(THEME_DARK_INVERSE_SURFACE),
        inverseOnSurface = Color(THEME_DARK_INVERSE_ON_SURFACE),
        inversePrimary = Color(THEME_DARK_INVERSE_PRIMARY)
    )

    fun getLightScheme() = lightColorScheme(
        primary = Color(THEME_LIGHT_PRIMARY),
        onPrimary = Color(THEME_LIGHT_ON_PRIMARY),
        primaryContainer = Color(THEME_LIGHT_PRIMARY_CONTAINER),
        onPrimaryContainer = Color(THEME_LIGHT_ON_PRIMARY_CONTAINER),

        secondary = Color(THEME_LIGHT_SECONDARY),
        onSecondary = Color(THEME_LIGHT_ON_SECONDARY),
        secondaryContainer = Color(THEME_LIGHT_SECONDARY_CONTAINER),
        onSecondaryContainer = Color(THEME_LIGHT_ON_SECONDARY_CONTAINER),

        tertiary = Color(THEME_LIGHT_TERTIARY),
        onTertiary = Color(THEME_LIGHT_ON_TERTIARY),
        tertiaryContainer = Color(THEME_LIGHT_TERTIARY_CONTAINER),
        onTertiaryContainer = Color(THEME_LIGHT_ON_TERTIARY_CONTAINER),

        error = Color(THEME_LIGHT_ERROR),
        errorContainer = Color(THEME_LIGHT_ERROR_CONTAINER),
        onError = Color(THEME_LIGHT_ON_ERROR),
        onErrorContainer = Color(THEME_LIGHT_ON_ERROR_CONTAINER),

        background = Color(THEME_LIGHT_BACKGROUND),
        onBackground = Color(THEME_LIGHT_ON_BACKGROUND),

        surface = Color(THEME_LIGHT_SURFACE),
        onSurface = Color(THEME_LIGHT_ON_SURFACE),
        surfaceVariant = Color(THEME_LIGHT_SURFACE_VARIANT),
        onSurfaceVariant = Color(THEME_LIGHT_ON_SURFACE_VARIANT),

        outline = Color(THEME_LIGHT_OUTLINE),
        inverseSurface = Color(THEME_LIGHT_INVERSE_SURFACE),
        inverseOnSurface = Color(THEME_LIGHT_INVERSE_ON_SURFACE),
        inversePrimary = Color(THEME_LIGHT_INVERSE_PRIMARY)
    )
}