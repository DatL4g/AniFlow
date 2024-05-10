package dev.datlag.aniflow.other

expect class BurningSeriesResolver {
    val isAvailable: Boolean
    fun resolveWatchedEpisodes(): Set<Episode>
    fun resolveByName(english: String?, romaji: String?): Set<Series>

    fun close()
}

data class Episode(
    val progress: Long,
    val length: Long,
    val number: String,
    val series: Series
)

data class Series(
    val title: String,
    val href: String
)