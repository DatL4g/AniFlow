package dev.datlag.aniflow.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

suspend fun <T> Flow<T>.safeFirstOrNull(): T? {
    return this.firstOrNull() ?: (this as? StateFlow<T>)?.value ?: this.firstOrNull()
}