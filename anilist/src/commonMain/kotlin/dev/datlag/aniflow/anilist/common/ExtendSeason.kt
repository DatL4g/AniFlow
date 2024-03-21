package dev.datlag.aniflow.anilist.common

import dev.datlag.aniflow.anilist.type.MediaSeason
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

internal fun MediaSeason.next(date: LocalDate): Pair<MediaSeason, Int> {
    return when (this) {
        MediaSeason.WINTER -> MediaSeason.SPRING to if (date.month == Month.DECEMBER) {
            date.year + 1
        } else {
            date.year
        }
        MediaSeason.SPRING -> MediaSeason.SUMMER to date.year
        MediaSeason.SUMMER -> MediaSeason.FALL to date.year
        MediaSeason.FALL -> MediaSeason.WINTER to date.year + 1
        else -> MediaSeason.UNKNOWN__ to date.year
    }
}