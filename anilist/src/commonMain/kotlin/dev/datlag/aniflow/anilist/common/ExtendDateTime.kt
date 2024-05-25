package dev.datlag.aniflow.anilist.common

import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.type.FuzzyDate
import dev.datlag.aniflow.anilist.type.MediaSeason
import kotlinx.datetime.*

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

internal val Instant.season: MediaSeason
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault()).month.season

internal val Instant.year: Int
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault()).year

internal val Instant.nextSeason: Pair<MediaSeason, Int>
    get() {
        val date = this.toLocalDateTime(TimeZone.currentSystemDefault())
        return date.month.season.next(date.date)
    }

internal fun PageMediaQuery.StartDate.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun AiringQuery.StartDate.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun MediumQuery.StartDate.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun ListQuery.StartDate.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun AiringQuery.StartedAt.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun PageMediaQuery.StartedAt.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun MediumQuery.StartedAt.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}

internal fun ListQuery.StartedAt.toLocalDate(): LocalDate? {
    return LocalDate(
        year = year ?: return null,
        monthNumber = month ?: return null,
        dayOfMonth = day ?: 1
    )
}
