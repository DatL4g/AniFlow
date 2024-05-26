package dev.datlag.aniflow.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import dev.datlag.aniflow.BuildKonfig
import dev.datlag.aniflow.Sekret
import dev.datlag.aniflow.other.StateSaver
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdOptions
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier

@Composable
actual fun AdView(id: String, type: AdType) {
    val nativeAdState = rememberCustomNativeAdState(
        adUnit = id,
        nativeAdOptions = NativeAdOptions.Builder()
            .setVideoOptions(
                VideoOptions.Builder()
                    .setStartMuted(true).setClickToExpandRequested(true)
                    .build()
            ).setRequestMultipleImages(true)
            .build(),
        adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Napier.e(p0.message)
            }
        }
    )

    val nativeAd by nativeAdState.nativeAd.collectAsStateWithLifecycle()

    nativeAd?.let { NativeAdCard(it, Modifier.fillMaxWidth()) }
}

actual object Ads {
    actual fun native(): String? {
        return if (StateSaver.sekretLibraryLoaded) {
            Sekret.androidAdNative(BuildKonfig.packageName)
        } else {
            null
        }
    }

    actual fun banner(): String? {
        return if (StateSaver.sekretLibraryLoaded) {
            Sekret.androidAdBanner(BuildKonfig.packageName)
        } else {
            null
        }
    }
}

@Composable
actual fun BannerAd(id: String, modifier: Modifier) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                adUnitId = id
                setAdSize(AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier
    )
}