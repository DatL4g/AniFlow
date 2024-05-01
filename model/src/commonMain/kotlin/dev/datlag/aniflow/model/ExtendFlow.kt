package dev.datlag.aniflow.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

suspend fun <T> Flow<T>.safeFirstOrNull(): T? {
    return this.firstOrNull() ?: (this as? StateFlow<T>)?.value ?: this.firstOrNull()
}

fun <T> Flow<T>.mutableStateIn(
    scope: CoroutineScope,
    initialValue: T
): MutableStateFlow<T> {
    val flow = MutableStateFlow(initialValue)

    scope.launch {
        this@mutableStateIn.collect(flow)
    }

    return flow
}

suspend fun <T> FlowCollector<T>.emitNotNull(value: T?) {
    if (value != null) {
        emit(value)
    }
}