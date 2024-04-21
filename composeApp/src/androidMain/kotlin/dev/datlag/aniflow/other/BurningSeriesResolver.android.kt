package dev.datlag.aniflow.other

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import io.github.aakira.napier.Napier

actual class BurningSeriesResolver(
    private val episodeClient: ContentProviderClient?,
    private val seriesClient: ContentProviderClient?
) {

    constructor(contentResolver: ContentResolver) : this(
        episodeClient = contentResolver.acquireContentProviderClient(episodesContentUri),
        seriesClient = contentResolver.acquireContentProviderClient(seriesContentUri)
    )

    constructor(context: Context) : this(context.contentResolver)

    actual fun resolveWatchedEpisodes(): Set<Episode> {
        if (episodeClient == null) {
            return emptySet()
        }

        val episodeCursor = episodeClient.query(
            episodesContentUri,
            null,
            "progress > 0 AND length > 0",
            null,
            null
        ) ?: return emptySet()

        val episodes = mutableSetOf<Episode>()

        if (episodeCursor.moveToFirst()) {
            while (!episodeCursor.isAfterLast) {
                val progressIndex = episodeCursor.getColumnIndex("progress")
                val lengthIndex = episodeCursor.getColumnIndex("length")
                val numberIndex = episodeCursor.getColumnIndex("number")
                val seriesHrefIndex = episodeCursor.getColumnIndex("seriesHref")

                if (progressIndex == -1 || lengthIndex == -1 || numberIndex == -1 || seriesHrefIndex == -1) {
                    episodeCursor.moveToNext()
                    continue
                }

                val progress = episodeCursor.getLong(progressIndex)
                val length = episodeCursor.getLong(lengthIndex)
                val number = episodeCursor.getString(numberIndex)
                val seriesHref = episodeCursor.getString(seriesHrefIndex)

                episodes.add(
                    Episode(
                        progress = progress,
                        length = length,
                        number = number,
                        series = Episode.Series(
                            title = seriesHref
                        )
                    )
                )

                episodeCursor.moveToNext()
            }
        }

        episodeCursor.close()
        return episodes
    }

    actual fun resolveByName(english: String?, romaji: String?) {
        val englishTrimmed = english?.trim()?.ifBlank { null }
        val romajiTrimmed = romaji?.trim()?.ifBlank { null }

        if (seriesClient == null || (englishTrimmed == null && romajiTrimmed == null)) {
            return
        }

        val selection = if (englishTrimmed != null && romajiTrimmed != null) {
            "title LIKE '%$englishTrimmed%' OR title LIKE '%$romajiTrimmed%'"
        } else if (englishTrimmed != null) {
            "title LIKE '%$englishTrimmed%'"
        } else {
            "title LIKE '%$romajiTrimmed%'"
        }
        val seriesCursor = seriesClient.query(
            seriesContentUri,
            null,
            selection,
            null,
            null
        ) ?: return

        if (seriesCursor.moveToFirst()) {
            while (!seriesCursor.isAfterLast) {
                val titleIndex = seriesCursor.getColumnIndex("title")

                if (titleIndex == -1) {
                    seriesCursor.moveToNext()
                    continue
                }

                val title = seriesCursor.getString(titleIndex)
                Napier.e("Series matching name: $title")

                seriesCursor.moveToNext()
            }
        }

        seriesCursor.close()
        return
    }

    actual fun close() {
        episodeClient?.close()
        seriesClient?.close()
    }

    companion object {
        val seriesContentUri = Uri.parse("content://dev.datlag.burningseries.provider/series")
        val episodesContentUri = Uri.parse("content://dev.datlag.burningseries.provider/episodes")
    }
}