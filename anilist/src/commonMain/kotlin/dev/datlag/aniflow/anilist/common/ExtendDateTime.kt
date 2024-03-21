package dev.datlag.aniflow.anilist.common

import dev.datlag.aniflow.anilist.type.MediaSeason
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal val Month.season: MediaSeason
    get() {
        return when (this) {
            Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> MediaSeason.WINTER
            Month.MARCH, Month.APRIL, Month.MAY -> MediaSeason.SPRING
            Month.JUNE, Month.JULY, Month.AUGUST -> MediaSeason.SUMMER
            Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> MediaSeason.FALL
            else -> MediaSeason.UNKNOWN__
        }
    }

internal val Instant.nextSeason: Pair<MediaSeason, Int>
    get() {
        val date = this.toLocalDateTime(TimeZone.currentSystemDefault())
        return date.month.season.next(date.date)
    }