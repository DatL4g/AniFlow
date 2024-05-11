package dev.datlag.aniflow.other

import androidx.compose.runtime.Composable

expect class InstantAppHelper {
    val isInstantApp: Boolean

    fun showInstallPrompt()
}

@Composable
expect fun rememberInstantAppHelper(): InstantAppHelper