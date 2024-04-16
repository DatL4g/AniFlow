package dev.datlag.aniflow.ui.navigation.screen.medium.component

import androidx.compose.runtime.Composable

@Composable
expect fun TranslateButton(
    text: String,
    onTranslation: (String?) -> Unit,
)