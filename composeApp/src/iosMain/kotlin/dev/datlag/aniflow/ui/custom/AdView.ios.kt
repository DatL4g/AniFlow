package dev.datlag.aniflow.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun BannerAd(id: String, modifier: Modifier) {
}

@Composable
actual fun AdView(id: String, type: AdType) {
}

actual object Ads {
    actual fun native(): String? {
        return null
    }

    actual fun banner(): String? {
        return null
    }
}