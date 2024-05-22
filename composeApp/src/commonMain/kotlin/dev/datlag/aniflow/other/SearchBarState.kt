package dev.datlag.aniflow.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class SearchBarState {
    var isActive by mutableStateOf(false)
}

@Composable
fun rememberSearchBarState(): SearchBarState {
    return remember { SearchBarState() }
}