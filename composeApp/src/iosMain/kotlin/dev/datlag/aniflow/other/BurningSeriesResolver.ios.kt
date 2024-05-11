package dev.datlag.aniflow.other

actual class BurningSeriesResolver {

    actual val isAvailable: Boolean
        get() = false

    actual fun resolveWatchedEpisodes(): Set<Episode> {
        // ToDo("Check if something like content provider exists")
        return emptySet()
    }

    actual fun resolveByName(english: String?, romaji: String?): Set<Series> {
        return emptySet()
    }

    actual fun close() { }
}