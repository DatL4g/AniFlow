package dev.datlag.aniflow.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual data object InstantAppHelper {
    actual val isInstantApp: Boolean
        get() = false

    actual fun showInstallPrompt() { }

}

@Composable
actual fun rememberInstantAppHelper(): InstantAppHelper {
    return remember { InstantAppHelper }
}