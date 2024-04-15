package dev.datlag.aniflow.ui.navigation.screen.initial.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf

sealed interface FABConfig {

    data class Scan(
        val listState: LazyListState,
        val onClick: () -> Unit
    ) : FABConfig

    companion object {
        val state = mutableStateOf<FABConfig?>(null)
    }
}