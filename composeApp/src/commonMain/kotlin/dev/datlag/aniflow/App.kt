package dev.datlag.aniflow

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.common.toComposeColor
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.ui.theme.Colors
import dev.datlag.aniflow.ui.theme.DynamicMaterialTheme
import dev.datlag.tooling.compose.toTypography
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.asFont
import org.kodein.di.DI
import org.kodein.di.instance

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }
val LocalEdgeToEdge = staticCompositionLocalOf<Boolean> { false }
val LocalDI = compositionLocalOf<DI> { error("No dependency injection provided") }
val LocalHaze = compositionLocalOf<HazeState> { error("No Haze state provided") }

@Composable
internal fun App(
    di: DI,
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDarkMode provides systemDarkTheme,
        LocalDI provides di
    ) {
        SystemAppearance()

        MaterialTheme(
            colorScheme = if (systemDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme(),
            typography = ManropeFontFamily().toTypography()
        ) {
            val appSettings by di.instance<Settings.PlatformAppSettings>()
            val savedColor by remember(appSettings) {
                appSettings.color
            }.collectAsStateWithLifecycle(null)
            val seedColor = remember(savedColor) { savedColor?.toComposeColor() }
            val allLoading by StateSaver.Home.isAllLoading.collectAsStateWithLifecycle(StateSaver.Home.currentAllLoading)
            val tempColor by StateSaver.temporaryColor.collectAsStateWithLifecycle()

            DynamicMaterialTheme(
                seedColor = tempColor?.toComposeColor() ?: seedColor,
                animate = !allLoading
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ManropeFontFamily(): FontFamily {
    val fonts = listOfNotNull(
        SharedRes.fonts.manrope_extra_light.asFont(FontWeight.ExtraLight),
        SharedRes.fonts.manrope_extra_light_italic.asFont(FontWeight.ExtraLight, FontStyle.Italic),

        SharedRes.fonts.manrope_light.asFont(FontWeight.Light),
        SharedRes.fonts.manrope_light_italic.asFont(FontWeight.Light, FontStyle.Italic),

        SharedRes.fonts.manrope_regular.asFont(FontWeight.Normal),
        SharedRes.fonts.manrope_regular_italic.asFont(FontWeight.Normal, FontStyle.Italic),

        SharedRes.fonts.manrope_medium.asFont(FontWeight.Medium),
        SharedRes.fonts.manrope_medium_italic.asFont(FontWeight.Medium, FontStyle.Italic),

        SharedRes.fonts.manrope_semi_bold.asFont(FontWeight.SemiBold),
        SharedRes.fonts.manrope_semi_bold_italic.asFont(FontWeight.SemiBold, FontStyle.Italic),

        SharedRes.fonts.manrope_bold.asFont(FontWeight.Bold),
        SharedRes.fonts.manrope_bold_italic.asFont(FontWeight.Bold, FontStyle.Italic),

        SharedRes.fonts.manrope_extra_bold.asFont(FontWeight.ExtraBold),
        SharedRes.fonts.manrope_extra_bold_italic.asFont(FontWeight.ExtraBold, FontStyle.Italic),
    )

    return FontFamily(fonts)
}

@Composable
expect fun SystemAppearance(isDark: Boolean = LocalDarkMode.current)