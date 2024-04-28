package dev.datlag.aniflow.ui.custom

import androidx.compose.runtime.Composable

@Composable
expect fun shareHandler(): ShareHandler

expect class ShareHandler {
    fun share(url: String?)
}