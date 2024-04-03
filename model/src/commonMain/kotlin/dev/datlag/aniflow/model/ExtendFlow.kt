package dev.datlag.aniflow.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

suspend fun <T> Flow<T>.saveFirstOrNull(): T? {
    return this.firstOrNull() ?: this.firstOrNull() ?: (this as? StateFlow<T>)?.value
}