package dev.datlag.aniflow.other

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.database.getStringOrNull
import dev.datlag.tooling.scopeCatching
import io.github.aakira.napier.Napier

actual class BurningSeriesResolver(
    private val packageManager: PackageManager,
    private val episodeClient: ContentProviderClient?,
    private val seriesClient: ContentProviderClient?
) {
    constructor(packageManager: PackageManager, contentResolver: ContentResolver) : this(
        packageManager = packageManager,
        episodeClient = contentResolver.acquireContentProviderClient(episodesContentUri),
        seriesClient = contentResolver.acquireContentProviderClient(seriesContentUri)
    )

    constructor(context: Context) : this(context.packageManager, context.contentResolver)

    actual val isAvailable: Boolean
        get() = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                packageManager.getApplicationInfo(packageName, 0)
            }
            true
        }.getOrNull() ?: false

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
                        series = Series(
                            title = seriesHref,
                            href = seriesHref
                        )
                    )
                )

                episodeCursor.moveToNext()
            }
        }

        episodeCursor.close()
        return episodes
    }

    actual fun resolveByName(english: String?, romaji: String?): Set<Series> {
        val englishTrimmed = english?.trim()?.ifBlank { null }?.replace("'", "")
        val romajiTrimmed = romaji?.trim()?.ifBlank { null }?.replace("'", "")

        if (seriesClient == null || (englishTrimmed == null && romajiTrimmed == null)) {
            return emptySet()
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
        ) ?: return emptySet()

        val series = mutableSetOf<Series>()

        if (seriesCursor.moveToFirst()) {
            while (!seriesCursor.isAfterLast) {
                val titleIndex = seriesCursor.getColumnIndex("title")
                val hrefIndex = seriesCursor.getColumnIndex("hrefPrimary")

                if (hrefIndex == -1) {
                    seriesCursor.moveToNext()
                    continue
                }

                val title = seriesCursor.getStringOrNull(titleIndex)
                val href = seriesCursor.getString(hrefIndex)

                series.add(
                    Series(
                        title = title ?: href,
                        href = href
                    )
                )

                seriesCursor.moveToNext()
            }
        }

        seriesCursor.close()
        return series
    }

    actual fun close() {
        episodeClient?.close()
        seriesClient?.close()
    }

    companion object {
        val packageName = "dev.datlag.burningseries"
        val seriesContentUri = Uri.parse("content://dev.datlag.burningseries.provider/series")
        val episodesContentUri = Uri.parse("content://dev.datlag.burningseries.provider/episodes")
    }
}