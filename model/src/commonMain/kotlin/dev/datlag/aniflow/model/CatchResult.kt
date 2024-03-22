package dev.datlag.aniflow.model

import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

sealed interface CatchResult<T> {

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun onError(callback: (Throwable?) -> Unit) = apply {
        if (this is Error) {
            callback(this.throwable)
        }
    }

    fun onSuccess(callback: (T & Any) -> Unit) = apply {
        if (this is Success) {
            callback(this.data)
        }
    }

    fun asSuccess(onError: (Throwable?) -> T & Any): T & Any {
        return if (this is Success) {
            this.data
        } else {
            onError((this as? Error)?.throwable)
        }
    }

    fun asNullableSuccess(onError: (Throwable?) -> T? = { null }): T? {
        return if (this is Success) {
            this.data
        } else {
            onError((this as? Error)?.throwable)
        }
    }

    fun asSuccessOrThrow(): T & Any {
        return if (this is Success) {
            this.data
        } else {
            throw (this as Error).throwable ?: IllegalStateException()
        }
    }

    fun asError(onSuccess: () -> Throwable? = { null }): Throwable? {
        return if (this is Error) {
            this.throwable
        } else {
            onSuccess()
        }
    }

    fun validate(predicate: (CatchResult<T>) -> Boolean): CatchResult<T> {
        return if (predicate(this)) {
            this
        } else {
            Error(null)
        }
    }

    fun validateSuccess(predicate: (T & Any) -> Boolean): CatchResult<T> {
        return when (this) {
            is Success -> {
                if (predicate(this.data)) {
                    this
                } else {
                    Error(null)
                }
            }
            else -> this
        }
    }

    suspend fun resultOnError(block: suspend CoroutineScope.() -> T): CatchResult<out T> {
        return when (this) {
            is Error -> result(block)
            else -> this
        }
    }

    suspend fun <M : Any> mapSuccess(block: suspend (T & Any) -> M?): CatchResult<M> {
        return when (this) {
            is Success -> {
                block(this.data)?.let(::Success) ?: Error(null)
            }
            else -> Error(null)
        }
    }

    data class Success<T>(
        val data: T & Any
    ) : CatchResult<T & Any>

    data class Error<T>(val throwable: Throwable?) : CatchResult<T>

    companion object {
        suspend fun <T> result(block: suspend CoroutineScope.() -> T): CatchResult<T & Any> = coroutineScope {
            val result = suspendCatching(block)
            return@coroutineScope if (result.isFailure) {
                Error(result.exceptionOrNull())
            } else {
                result.getOrNull()?.let {
                    Success(it)
                } ?: Error(result.exceptionOrNull())
            }
        }
    }
}