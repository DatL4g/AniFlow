package dev.datlag.aniflow.common

import kotlinx.datetime.*
import kotlin.math.abs

fun LocalDateTime.formatNext(
    compare: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
): String {
    val period = this.toInstant(
        TimeZone.currentSystemDefault()
    ).periodUntil(
        compare.toInstant(
            TimeZone.currentSystemDefault()
        ),
        TimeZone.currentSystemDefault()
    )

    val dayDiff = abs(period.days)
    val monthDiff = abs(period.months)
    val yearDiff = abs(period.years)

    return buildString {
        var combine = false

        if (dayDiff >= 1 || monthDiff >= 1 || yearDiff >= 1) {
            combine = true
            append(this@formatNext.dayOfMonth)
            append('.')
            append(' ')
        }
        if (dayDiff >= 1 || monthDiff >= 1 || yearDiff >= 1) {
            combine = true
            append(this@formatNext.monthNumber)
            append('.')
            append(' ')
        }
        if (yearDiff >= 1) {
            combine = true
            append(this@formatNext.year)
            append(' ')
        }
        if (combine) {
            append('-')
            append(' ')
        }

        val hourString = if (this@formatNext.hour <= 9) {
            "0${this@formatNext.hour}"
        } else {
            this@formatNext.hour
        }
        val minuteString = if (this@formatNext.minute <= 9) {
            "0${this@formatNext.minute}"
        } else {
            this@formatNext.minute
        }
        append(hourString)
        append(':')
        append(minuteString)
    }
}