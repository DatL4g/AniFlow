package dev.datlag.aniflow.trace

import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.trace.model.SearchResponse
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

class TraceRepository(
    private val trace: Trace
) {

    private val byteArray = MutableStateFlow<ByteArray?>(null)
    val response: Flow<State> = byteArray.transform {
        return@transform if (it == null || it.isEmpty()) {
            emit(State.None)
        } else {
            emit(
                State.fromResponse(
                    CatchResult.repeat(2) {
                        trace.search(it)
                    }.asNullableSuccess()
                )
            )
        }
    }

    fun clear() = byteArray.update { null }
    fun search(array: ByteArray) = byteArray.update { array }

    @Serializable
    sealed interface State {
        @Serializable
        data object None : State

        @Serializable
        data class Success(
            val response: SearchResponse,
        ) : State

        @Serializable
        data object Error : State

        companion object {
            fun fromResponse(response: SearchResponse?): State {
                return if (response == null || response.isError) {
                    Error
                } else {
                    Success(response)
                }
            }
        }
    }
}