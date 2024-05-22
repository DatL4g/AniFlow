package dev.datlag.aniflow.anilist.common

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaType

fun Optional.Companion.presentIf(value: Boolean) = if (value) {
    present(value)
} else {
    absent()
}

fun Optional.Companion.presentIfNot(value: Boolean) = if (value) {
    absent()
} else {
    present(value)
}

fun Optional.Companion.presentMediaType(type: MediaType) = when (type) {
    MediaType.UNKNOWN__ -> absent()
    else -> present(type)
}

fun Optional.Companion.presentMediaSeason(type: MediaSeason) = when (type) {
    MediaSeason.UNKNOWN__ -> absent()
    else -> present(type)
}

fun <V> Optional.Companion.presentIf(predicate: Boolean, value: V) = if (predicate) {
    present(value)
} else {
    absent()
}

fun <V> Optional.Companion.presentIfNot(predicate: Boolean, value: V) = if (predicate) {
    absent()
} else {
    present(value)
}

fun <V> Optional.Companion.presentAsList(vararg value: V) = present(listOf(*value))