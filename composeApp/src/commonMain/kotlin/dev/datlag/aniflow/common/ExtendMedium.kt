package dev.datlag.aniflow.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import dev.datlag.aniflow.LocalDI
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.*
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.trace.model.SearchResponse
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.StringResource
import org.kodein.di.instance
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar

fun Medium.preferred(setting: SettingsTitle?): String {
    return this.title.preferred(setting).ifBlank { this.id.toString() }
}

fun Medium.notPreferred(setting: SettingsTitle?): String? {
    return this.title.notPreferred(setting)?.ifBlank { null }
}

expect fun Medium.Title.preferred(setting: SettingsTitle?): String

fun Medium.Title.notPreferred(setting: SettingsTitle?): String? {
    val preferred = this.preferred(setting).trim()
    val notPreferred =  when {
        this.native?.trim().equals(preferred, ignoreCase = true) -> {
            when {
                this.english == null -> this.romaji
                this.english?.trim().equals(preferred, ignoreCase = true) -> this.romaji
                else -> this.english
            }
        }
        this.romaji?.trim().equals(preferred, ignoreCase = true) -> {
            when {
                this.native == null -> this.english
                this.native?.trim().equals(preferred, ignoreCase = true) -> this.english
                else -> this.native
            }
        }
        this.english?.trim().equals(preferred, ignoreCase = true) -> {
            when {
                this.native == null -> this.romaji
                this.native?.trim().equals(preferred, ignoreCase = true) -> this.romaji
                else -> this.native
            }
        }
        else -> null
    }

    return when {
        notPreferred == null -> null
        notPreferred.trim().equals(preferred, ignoreCase = true) -> null
        else -> notPreferred
    }
}

fun MediaFormat.icon(): ImageVector = when (this) {
    MediaFormat.MANGA, MediaFormat.NOVEL, MediaFormat.ONE_SHOT -> Icons.AutoMirrored.Rounded.MenuBook
    MediaFormat.MUSIC -> Icons.Rounded.MusicNote
    else -> Icons.Rounded.OndemandVideo
}

fun MediaFormat.text(): StringResource {
    return when (this) {
        MediaFormat.TV -> SharedRes.strings.media_format_tv
        MediaFormat.TV_SHORT -> SharedRes.strings.media_format_tv_short
        MediaFormat.MOVIE -> SharedRes.strings.media_format_movie
        MediaFormat.SPECIAL -> SharedRes.strings.media_format_special
        MediaFormat.OVA -> SharedRes.strings.media_format_ova
        MediaFormat.ONA -> SharedRes.strings.media_format_ona
        MediaFormat.MUSIC -> SharedRes.strings.media_format_music
        MediaFormat.MANGA -> SharedRes.strings.media_format_manga
        MediaFormat.NOVEL -> SharedRes.strings.media_format_novel
        MediaFormat.ONE_SHOT -> SharedRes.strings.media_format_one_shot
        MediaFormat.UNKNOWN__ -> SharedRes.strings.unknown
    }
}

fun Medium.formatText(): StringResource {
    return this.format.text()
}

fun MediaStatus.text(): StringResource {
    return when (this) {
        MediaStatus.FINISHED -> SharedRes.strings.media_status_finished
        MediaStatus.RELEASING -> SharedRes.strings.media_status_releasing
        MediaStatus.NOT_YET_RELEASED -> SharedRes.strings.media_status_not_yet_released
        MediaStatus.CANCELLED -> SharedRes.strings.media_status_cancelled
        MediaStatus.HIATUS -> SharedRes.strings.media_status_hiatus
        MediaStatus.UNKNOWN__ -> SharedRes.strings.unknown
    }
}

fun Medium.statusText(): StringResource {
    return this.status.text()
}

fun Collection<Medium.Ranking>.rated(): Medium.Ranking? {
    val filtered = this.filter { it.type == MediaRankType.RATED }

    return filtered.firstOrNull {
        it.allTime
    } ?: filtered.sortedWith(
        compareByDescending<Medium.Ranking> {
            it.year
        }.thenBy {
            it.season
        }
    ).firstOrNull()
}

fun Medium.rated(): Medium.Ranking? = this.ranking.rated()

fun Collection<Medium.Ranking>.popular(): Medium.Ranking? {
    val filtered = this.filter { it.type == MediaRankType.POPULAR }

    return filtered.firstOrNull {
        it.allTime
    } ?: filtered.sortedWith(
        compareByDescending<Medium.Ranking> {
            it.year
        }.thenBy {
            it.season
        }
    ).firstOrNull()
}

fun Medium.popular(): Medium.Ranking? = this.ranking.popular()

expect fun Character.Name.preferred(setting: SettingsChar?): String

fun Character.preferredName(settings: SettingsChar?): String = this.name.preferred(settings)

private fun SearchResponse.Result.AniList.Title?.asMediumTitle(): Medium.Title {
    return Medium.Title(
        native = this?.native,
        english = this?.english,
        romaji = this?.romaji,
        userPreferred = null
    )
}

fun SearchResponse.Result.AniList.asMedium(): Medium {
    return Medium(
        id = this.id,
        idMal = this.idMal,
        _isAdult = this.isAdult,
        title = this.title.asMediumTitle()
    )
}

fun MediaListStatus.icon() = when (this) {
    MediaListStatus.CURRENT -> Icons.Rounded.Edit
    MediaListStatus.COMPLETED -> Icons.Rounded.Check
    MediaListStatus.PAUSED -> Icons.Rounded.Pause
    MediaListStatus.DROPPED -> Icons.Rounded.Close
    MediaListStatus.PLANNING -> Icons.Rounded.WatchLater
    MediaListStatus.REPEATING -> Icons.Rounded.Replay
    else -> Icons.Rounded.Add
}

fun MediaListStatus.stringRes(isManga: Boolean) = when (this) {
    MediaListStatus.CURRENT -> if (isManga) SharedRes.strings.reading else SharedRes.strings.watching
    MediaListStatus.COMPLETED -> SharedRes.strings.completed
    MediaListStatus.PAUSED -> SharedRes.strings.paused
    MediaListStatus.DROPPED -> SharedRes.strings.dropped
    MediaListStatus.PLANNING -> SharedRes.strings.planning
    MediaListStatus.REPEATING -> SharedRes.strings.repeating
    else -> SharedRes.strings.add
}

fun MediaListStatus.stringRes(type: MediaType) = this.stringRes(type == MediaType.MANGA)

fun MediaType.stringRes() = when (this) {
    MediaType.MANGA -> SharedRes.strings.manga
    else -> SharedRes.strings.anime
}