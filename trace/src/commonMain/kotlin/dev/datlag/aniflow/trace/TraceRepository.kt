package dev.datlag.aniflow.trace

import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.trace.model.SearchResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

class TraceRepository(
    private val trace: Trace,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    private val byteArray = MutableStateFlow<ByteArray?>(null)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val response: Flow<State> = combine(byteArray, nsfw.distinctUntilChanged()) { t1, t2 ->
        t1 to t2
    }.transformLatest { (t1, t2) ->
        return@transformLatest if (t1 == null || t1.isEmpty()) {
            emit(State.None)
        } else {
            emit(
                State.fromResponse(
                    response = CatchResult.repeat(2) {
                        trace.search(t1)
                    }.asNullableSuccess(),
                    nsfw = t2
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
            fun fromResponse(response: SearchResponse?, nsfw: Boolean): State {
                val nsfwAware = response?.nsfwAware(nsfw)

                return if (nsfwAware == null || nsfwAware.isError) {
                    Error
                } else {
                    Success(nsfwAware)
                }
            }
        }
    }
}