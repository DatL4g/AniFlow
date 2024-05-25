package dev.datlag.aniflow.trace.model

import dev.datlag.aniflow.model.serializer.SerializableImmutableList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.serialization.*

@Serializable
data class SearchResponse(
    @SerialName("error") val error: String? = null,
    @SerialName("result") val result: SerializableImmutableList<Result> = persistentListOf()
) {

    @Transient
    val isError = !error.isNullOrBlank() || result.isEmpty()

    @Transient
    val combinedResults: ImmutableSet<CombinedResult> = result.groupBy { it.aniList.id }.mapValues { entry ->
        CombinedResult(
            aniList = Result.AniList(
                id = entry.key,
                idMal = entry.value.firstNotNullOfOrNull { it.aniList.idMal },
                isAdult = entry.value.any { it.aniList.isAdult },
                title = entry.value.firstNotNullOfOrNull { it.aniList.title }
            ),
            maxSimilarity = entry.value.maxOf { it.similarity },
            avgSimilarity = entry.value.map { it.similarity }.average().toFloat()
        )
    }.values.toImmutableSet()

    fun nsfwAware(allowed: Boolean): SearchResponse {
        return if (allowed) {
            this
        } else {
            SearchResponse(
                error = error,
                result = result.filterNot { it.aniList.isAdult }.toImmutableList()
            )
        }
    }

    @Serializable
    data class Result(
        @SerialName("anilist") val aniList: AniList,
        @SerialName("similarity") val similarity: Float = 0F,
    ) {

        @Serializable
        data class AniList(
            @SerialName("id") val id: Int,
            @SerialName("idMal") val idMal: Int?,
            @SerialName("isAdult") val isAdult: Boolean = false,
            @SerialName("title") val title: Title? = null,
        ) {

            @Serializable
            data class Title(
                @SerialName("native") val native: String? = null,
                @SerialName("romaji") val romaji: String? = null,
                @SerialName("english") val english: String? = null,
            )
        }
    }

    data class CombinedResult(
        val aniList: Result.AniList,
        val maxSimilarity: Float,
        val avgSimilarity: Float = maxSimilarity,
    ) {
        val isSingle: Boolean = maxSimilarity == avgSimilarity
        val maxPercentage: String = "${(maxSimilarity * 100F).toInt()}%"
        val avgPercentage: String = "${(avgSimilarity * 100F).toInt()}%"
    }
}
