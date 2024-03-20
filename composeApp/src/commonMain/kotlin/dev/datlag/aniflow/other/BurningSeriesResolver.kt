package dev.datlag.aniflow.other

expect class BurningSeriesResolver {
    fun resolveWatchedEpisodes(): Set<Episode>

    fun close()
}

data class Episode(
    val progress: Long,
    val length: Long,
    val number: String,
    val series: Series
) {
    data class Series(
        val title: String
    )
}