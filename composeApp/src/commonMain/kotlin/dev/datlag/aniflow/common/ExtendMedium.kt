package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.model.Medium

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