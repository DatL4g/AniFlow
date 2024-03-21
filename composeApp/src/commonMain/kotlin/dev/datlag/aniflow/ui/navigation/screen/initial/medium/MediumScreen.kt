package dev.datlag.aniflow.ui.navigation.screen.initial.medium

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.datlag.aniflow.common.preferredTitle

@Composable
fun MediumScreen(component: MediumComponent) {
    Text(text = component.initialMedium.preferredTitle())
}