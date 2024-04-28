package dev.datlag.aniflow.ui.custom

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun shareHandler(): ShareHandler {
    val context = LocalContext.current
    return remember(context) {
        ShareHandler(context)
    }
}

actual class ShareHandler(
    private val context: Context
) {
    actual fun share(url: String?) {
        if (!url.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)

            ContextCompat.startActivity(
                context,
                Intent.createChooser(intent, null),
                null
            )
        }
    }
}