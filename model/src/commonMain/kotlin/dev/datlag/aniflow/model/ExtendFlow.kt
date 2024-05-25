package dev.datlag.aniflow.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> Flow<T>.safeFirstOrNull(): T? {
    return this.firstOrNull() ?: (this as? StateFlow<T>)?.value ?: this.firstOrNull()
}

fun <T> Flow<T>.mutableStateIn(
    context: CoroutineContext = EmptyCoroutineContext,
    scope: CoroutineScope,
    initialValue: T
): MutableStateFlow<T> {
    val flow = MutableStateFlow(initialValue)

    scope.launch(context) {
        this@mutableStateIn.collect(flow)
    }

    return flow
}

suspend fun <T> FlowCollector<T>.emitNotNull(value: T?) {
    if (value != null) {
        emit(value)
    }
}

inline fun <T : Collection<*>> Flow<T>.mapNotEmpty(): Flow<T> = transform { value ->
    if (value.isNotEmpty()) {
        return@transform emit(value)
    }
}