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
    val bestResult: Result? = result.maxByOrNull { it.similarity }

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
}
