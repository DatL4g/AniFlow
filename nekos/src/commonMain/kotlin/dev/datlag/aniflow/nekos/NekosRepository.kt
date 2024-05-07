package dev.datlag.aniflow.nekos

import dev.datlag.aniflow.model.CatchResult
import dev.datlag.aniflow.nekos.model.ImagesResponse
import dev.datlag.aniflow.nekos.model.Rating
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

class NekosRepository(
    private val nekos: Nekos,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    private val offset = MutableStateFlow<Int?>(null)
    val rating = MutableStateFlow<Rating>(Rating.Safe)

    private val result = combine(offset, rating) { o, r ->
        CatchResult.repeat(2) {
            nekos.images(
                rating = r.query,
                offset = o,
            )
        }
    }.map {
        State.fromResponse(it.asNullableSuccess())
    }
    val response = combine(nsfw.distinctUntilChanged(), result, rating) { n, q, r ->
        if (n) {
            q
        } else {
            if (r is Rating.Explicit || r is Rating.Borderline) {
                rating(Rating.Safe)
            }

            when (q) {
                is State.Success -> {
                    State.Success(
                        ImagesResponse(
                            items = q.response.items.filterNot { it.hasAdultTag },
                            count = q.response.count
                        )
                    )
                }
                else -> q
            }
        }
    }

    fun offset(value: Int) {
        offset.update { value }
    }

    fun rating(value: Rating) {
        rating.update {
            if (it != value) {
                offset.update { null }
            }
            value
        }
    }

    @Serializable
    sealed interface State {
        @Serializable
        data object None : State

        @Serializable
        data class Success(
            val response: ImagesResponse
        ) : State

        @Serializable
        data object Error : State

        companion object {
            fun fromResponse(response: ImagesResponse?): State {
                return if (response == null || response.isError) {
                    Error
                } else {
                    Success(response)
                }
            }
        }
    }
}