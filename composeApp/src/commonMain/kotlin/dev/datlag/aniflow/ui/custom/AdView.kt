package dev.datlag.aniflow.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AdView(
    id: String,
    type: AdType
)

@Composable
fun NativeAdView() {
    Ads.native()?.let {
        AdView(
            id = it,
            type = AdType.Native
        )
    }
}

@Composable
expect fun BannerAd(
    id: String,
    modifier: Modifier = Modifier
)

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    Ads.banner()?.let {
        BannerAd(
            id = it,
            modifier = modifier
        )
    }
}

expect object Ads {
    fun native(): String?
    fun banner(): String?
}

sealed interface AdType {
    data object Native : AdType
    data object Banner : AdType
}