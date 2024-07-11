package dev.datlag.aniflow.other

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.database.getStringOrNull
import dev.datlag.tooling.scopeCatching
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet

actual class BurningSeriesResolver(
    private val packageManager: PackageManager,
    private val episodeClient: ContentProviderClient?,
    private val seriesClient: ContentProviderClient?
) {
    constructor(packageManager: PackageManager, contentResolver: ContentResolver) : this(
        packageManager = packageManager,
        episodeClient = scopeCatching {
            contentResolver.acquireContentProviderClient(episodesContentUri)
        }.getOrNull(),
        seriesClient = scopeCatching {
            contentResolver.acquireContentProviderClient(seriesContentUri)
        }.getOrNull()
    )

    constructor(context: Context) : this(context.packageManager, context.contentResolver)

    private val packageInfo: PackageInfo? = scopeCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }.getOrNull()

    @Suppress("DEPRECATION")
    actual val versionCode: Int
        get() {
            return (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode?.toInt() ?: packageInfo?.versionCode
            } else {
                packageInfo?.versionCode
            }) ?: -1
        }

    actual val versionName: String? = packageInfo?.versionName?.ifBlank { null }

    actual val isAvailable: Boolean
        get() = packageInfo != null

    actual fun resolveWatchedEpisode(seriesHref: String): Int? = scopeCatching {
        if (episodeClient == null) {
            return@scopeCatching null
        }

        val episodeCursor = episodeClient.query(
            episodesContentUri,
            arrayOf("MAX(number) as number"),
            "finished = 1 AND seriesHref = '$seriesHref'",
            null,
            "number DESC"
        ) ?: return@scopeCatching null

        var max: Int? = null
        if (episodeCursor.moveToFirst()) {
            while (!episodeCursor.isAfterLast) {
                val numberIndex = episodeCursor.getColumnIndex("number")

                if (numberIndex == -1) {
                    episodeCursor.moveToNext()
                    continue
                }

                val number = episodeCursor.getInt(numberIndex)
                if (max == null || number > max) {
                    max = number
                }

                episodeCursor.moveToNext()
            }
        }

        episodeCursor.close()
        return@scopeCatching max
    }.getOrNull()

    actual fun resolveByName(english: String?, romaji: String?): ImmutableSet<Series> {
        val englishTrimmed = english?.trim()?.ifBlank { null }?.replace("'", "")?.trim()
        val romajiTrimmed = romaji?.trim()?.ifBlank { null }?.replace("'", "")?.trim()

        if (seriesClient == null || (englishTrimmed == null && romajiTrimmed == null)) {
            return persistentSetOf()
        }

        val selection = if (englishTrimmed != null && romajiTrimmed != null) {
            "isAnime = 1 AND fullTitle LIKE '%$englishTrimmed%' OR fullTitle LIKE '%$romajiTrimmed%'"
        } else if (englishTrimmed != null) {
            "isAnime = 1 AND fullTitle LIKE '%$englishTrimmed%'"
        } else {
            "isAnime = 1 AND fullTitle LIKE '%$romajiTrimmed%'"
        }

        return seriesBySelection(selection).ifEmpty {
            val mainTitleSelection = if (englishTrimmed != null && romajiTrimmed != null) {
                "isAnime = 1 AND mainTitle LIKE '%$englishTrimmed%' OR mainTitle LIKE '%$romajiTrimmed%'"
            } else if (englishTrimmed != null) {
                "isAnime = 1 AND mainTitle LIKE '%$englishTrimmed%'"
            } else {
                "isAnime = 1 AND mainTitle LIKE '%$romajiTrimmed%'"
            }

            seriesBySelection(mainTitleSelection).ifEmpty {
                val subTitleSelection = if (englishTrimmed != null && romajiTrimmed != null) {
                    "isAnime = 1 AND subTitle LIKE '%$englishTrimmed%' OR subTitle LIKE '%$romajiTrimmed%'"
                } else if (englishTrimmed != null) {
                    "isAnime = 1 AND subTitle LIKE '%$englishTrimmed%'"
                } else {
                    "isAnime = 1 AND subTitle LIKE '%$romajiTrimmed%'"
                }

                seriesBySelection(subTitleSelection)
            }
        }
    }

    actual fun resolveByName(value: String): ImmutableSet<Series> {
        val trimmed = value.trim().replace("'", "").trim()

        return if (trimmed.length >= 3) {
            seriesBySelection("isAnime = 1 AND fullTitle LIKE '%$trimmed%'")
        } else {
            persistentSetOf()
        }
    }

    private fun seriesBySelection(selection: String): ImmutableSet<Series> = scopeCatching {
        if (seriesClient == null) {
            return@scopeCatching persistentSetOf()
        }

        val seriesCursor = seriesClient.query(
            seriesContentUri,
            null,
            selection,
            null,
            null
        ) ?: return@scopeCatching persistentSetOf()

        val series = mutableSetOf<Series>()

        if (seriesCursor.moveToFirst()) {
            while (!seriesCursor.isAfterLast) {
                val titleIndex = seriesCursor.getColumnIndex("fullTitle")
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
        return@scopeCatching series.toImmutableSet()
    }.getOrNull() ?: persistentSetOf()

    actual fun close() {
        scopeCatching {
            episodeClient?.close()
            seriesClient?.close()
        }
    }

    companion object {
        val packageName = "dev.datlag.burningseries"
        val seriesContentUri = Uri.parse("content://dev.datlag.burningseries.provider/series")
        val episodesContentUri = Uri.parse("content://dev.datlag.burningseries.provider/episodes")
    }
}