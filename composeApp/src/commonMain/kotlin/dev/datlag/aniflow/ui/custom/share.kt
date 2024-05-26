package dev.datlag.aniflow.ui.custom

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

@Composable
expect fun shareHandler(): ShareHandler

expect class ShareHandler {

    val domainVerifier: StateFlow<Boolean>
    val domainVerifierSupported: Boolean

    fun share(url: String?)
    fun checkDomain()
    fun enableDomain()
}