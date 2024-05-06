package dev.datlag.aniflow.ui.navigation.screen.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@Composable
fun AllLoadingView(
    modifier: Modifier = Modifier.fillMaxSize(),
    loadingContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val allLoading by StateSaver.Home.isAllLoading.collectAsStateWithLifecycle(StateSaver.Home.currentAllLoading)

        content()
        if (allLoading) {
            loadingContent()
        }
    }
}