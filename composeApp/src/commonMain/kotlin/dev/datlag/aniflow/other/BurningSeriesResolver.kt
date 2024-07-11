package dev.datlag.aniflow.other

import kotlinx.collections.immutable.ImmutableSet

expect class BurningSeriesResolver {
    val isAvailable: Boolean
    val versionCode: Int
    val versionName: String?

    fun resolveWatchedEpisode(seriesHref: String): Int?
    fun resolveByName(english: String?, romaji: String?): ImmutableSet<Series>
    fun resolveByName(value: String): ImmutableSet<Series>

    fun close()
}

data class Series(
    val title: String,
    val href: String
)