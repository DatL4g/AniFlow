package dev.datlag.aniflow.trace.model

import kotlinx.serialization.*

@Serializable
data class SearchResponse(
    @SerialName("error") val error: String? = null,
    @SerialName("result") val result: List<Result> = emptyList()
) {

    @Transient
    val isError = !error.isNullOrBlank()

    @Transient
    val combinedResults: Set<CombinedResult> = result.groupBy { it.aniList.id }.mapValues { entry ->
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
    }.values.toSet()

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
