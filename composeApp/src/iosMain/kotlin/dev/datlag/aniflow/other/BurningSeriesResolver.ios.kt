package dev.datlag.aniflow.other

actual class BurningSeriesResolver {
    actual fun resolveWatchedEpisodes(): Set<Episode> {
        // ToDo("Check if something like content provider exists")
        return emptySet()
    }

    actual fun resolveByName(english: String?, romaji: String?) { }

    actual fun close() { }
}