package dev.datlag.aniflow.common

import androidx.compose.runtime.Composable
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaRankType
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.icerock.moko.resources.StringResource

fun Medium.preferred(): String {
    return this.title.preferred().ifBlank { this.id.toString() }
}

fun Medium.notPreferred(): String? {
    return this.title.notPreferred()?.ifBlank { null }
}

expect fun Medium.Title.preferred(): String

fun Medium.Title.notPreferred(): String? {
    val preferred = this.preferred().trim()
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

fun Medium.Full.formatText(): StringResource {
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

fun Medium.Full.statusText(): StringResource {
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

fun Medium.Full.rated(): Medium.Ranking? = this.ranking.rated()

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

fun Medium.Full.popular(): Medium.Ranking? = this.ranking.popular()

expect fun Medium.Character.Name.preferred(): String

fun Medium.Character.preferredName(): String = this.name.preferred()
