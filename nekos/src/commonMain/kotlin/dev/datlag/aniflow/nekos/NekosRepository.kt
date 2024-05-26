package dev.datlag.aniflow.nekos

import dev.datlag.aniflow.nekos.model.ImagesResponse
import dev.datlag.aniflow.nekos.model.Rating
import dev.datlag.tooling.async.suspendCatching
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

class NekosRepository(
    private val nekos: Nekos,
    private val nsfw: Flow<Boolean> = flowOf(false),
) {

    val rating = MutableStateFlow<Rating>(Rating.Safe)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val result = rating.mapLatest {
        suspendCatching {
            nekos.images(
                rating = it.query
            )
        }
    }.mapLatest {
        State.fromResponse(it.getOrNull())
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
                            items = q.response.items.filterNot { it.hasAdultTag }.toImmutableList(),
                            count = q.response.count
                        )
                    )
                }
                else -> q
            }
        }
    }

    fun rating(value: Rating) {
        rating.update { value }
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