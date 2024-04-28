package dev.datlag.aniflow.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
actual fun shareHandler(): ShareHandler {
    val uriHandler = LocalUriHandler.current
    return remember(uriHandler) {
        ShareHandler(uriHandler)
    }
}

actual class ShareHandler(
    private val uriHandler: UriHandler
) {
    actual fun share(url: String?) {
        if (!url.isNullOrBlank()) {
            uriHandler.openUri(url)
        }
    }
}