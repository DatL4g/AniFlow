package dev.datlag.aniflow.other

import kotlinx.collections.immutable.ImmutableSet

expect class BurningSeriesResolver {
    val isAvailable: Boolean
    fun resolveWatchedEpisodes(): ImmutableSet<Episode>
    fun resolveByName(english: String?, romaji: String?): ImmutableSet<Series>
    fun resolveByName(value: String): ImmutableSet<Series>

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