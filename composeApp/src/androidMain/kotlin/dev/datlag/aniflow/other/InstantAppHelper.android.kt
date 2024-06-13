package dev.datlag.aniflow.other

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.instantapps.InstantApps
import com.google.android.gms.common.wrappers.InstantApps as CommonInstantApps
import dev.datlag.aniflow.findActivity

actual class InstantAppHelper(private val context: Context) {

    actual val isInstantApp: Boolean
        get() = CommonInstantApps.isInstantApp(context)

    actual fun showInstallPrompt() {
        context.findActivity()?.let {
            InstantApps.showInstallPrompt(it, null, 1337, null)
        }
    }
}

@Composable
actual fun rememberInstantAppHelper(): InstantAppHelper {
    val context = LocalContext.current

    return remember(context) { InstantAppHelper(context) }
}