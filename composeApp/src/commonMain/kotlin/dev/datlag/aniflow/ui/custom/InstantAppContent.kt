package dev.datlag.aniflow.ui.custom

import androidx.compose.runtime.Composable
import dev.datlag.aniflow.other.InstantAppHelper
import dev.datlag.aniflow.other.rememberInstantAppHelper

@Composable
fun InstantAppContent(
    onInstantApp: @Composable (InstantAppHelper) -> Unit = {},
    content: @Composable () -> Unit
) {
    val helper = rememberInstantAppHelper()

    if (helper.isInstantApp) {
        onInstantApp(helper)
    } else {
        content()
    }
}