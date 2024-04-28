package dev.datlag.aniflow.common

import androidx.compose.ui.graphics.Color
import dev.datlag.aniflow.settings.model.AppSettings

@OptIn(ExperimentalStdlibApi::class)
fun AppSettings.Color.toComposeColor() = Color(
    this.hex.substringAfter('#').hexToLong() or 0x00000000FF000000
)