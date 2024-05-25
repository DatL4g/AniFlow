package dev.datlag.aniflow.other

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

actual class BurningSeriesResolver {

    actual val isAvailable: Boolean
        get() = false

    actual fun resolveWatchedEpisodes(): ImmutableSet<Episode> {
        // ToDo("Check if something like content provider exists")
        return persistentSetOf()
    }

    actual fun resolveByName(english: String?, romaji: String?): ImmutableSet<Series> {
        return persistentSetOf()
    }

    actual fun resolveByName(value: String): ImmutableSet<Series> {
        return persistentSetOf()
    }

    actual fun close() { }
}