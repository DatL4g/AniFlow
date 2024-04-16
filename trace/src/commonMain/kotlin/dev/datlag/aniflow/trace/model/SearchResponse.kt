package dev.datlag.aniflow.trace.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SearchResponse(
    @SerialName("error") val error: String? = null,
    @SerialName("result") val result: List<Result> = emptyList()
) {

    @Transient
    val isError = !error.isNullOrBlank()

    @Transient
    private val onlyHighResults: List<Result> = result.filter {
        it.similarity >= 0.9F
    }

    @Transient
    private val groupedResults: Map<Int, List<Result>> = onlyHighResults.groupBy {
        it.aniList
    }

    @Transient
    val bestResult: Result? = groupedResults.maxByOrNull {
        it.value.map { v -> v.similarity }.average()
    }?.value?.maxByOrNull { it.similarity }

    @Serializable
    data class Result(
        @SerialName("anilist") val aniList: Int,
        @SerialName("similarity") val similarity: Float = 0F,
    )
}
