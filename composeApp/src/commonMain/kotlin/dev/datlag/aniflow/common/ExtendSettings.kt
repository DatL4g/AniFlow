package dev.datlag.aniflow.common

import androidx.compose.ui.graphics.Color
import dev.datlag.aniflow.SharedRes
import dev.icerock.moko.resources.StringResource
import dev.datlag.aniflow.settings.model.AppSettings

@OptIn(ExperimentalStdlibApi::class)
fun AppSettings.Color.toComposeColor() = Color(
    this.hex.substringAfter('#').hexToLong() or 0x00000000FF000000
)

fun AppSettings.Color.toComposeString(): StringResource = when (this) {
    is AppSettings.Color.Blue -> SharedRes.strings.color_blue
    is AppSettings.Color.Purple -> SharedRes.strings.color_purple
    is AppSettings.Color.Pink -> SharedRes.strings.color_pink
    is AppSettings.Color.Orange -> SharedRes.strings.color_orange
    is AppSettings.Color.Red -> SharedRes.strings.color_red
    is AppSettings.Color.Green -> SharedRes.strings.color_green
    is AppSettings.Color.Gray -> SharedRes.strings.color_gray
    is AppSettings.Color.Custom -> SharedRes.strings.color_custom
}