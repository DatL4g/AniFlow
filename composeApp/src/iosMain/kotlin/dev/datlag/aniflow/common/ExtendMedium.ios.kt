package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.Character

actual fun Medium.Title.preferred(): String {
    return this.userPreferred?.ifBlank { null }
        ?: this.english?.ifBlank { null }
        ?: this.romaji?.ifBlank { null }
        ?: this.native?.ifBlank { null }
        ?: ""
}

actual fun Character.Name.preferred(): String {
    return this.userPreferred?.ifBlank { null }
        ?: this.full?.ifBlank { null }
        ?: buildString {
            append(this@preferred.first)
            append(" ")
            append(this@preferred.middle)
            append(" ")
            append(this@preferred.last)
        }.ifBlank { null }
        ?: this.native?.ifBlank { null }
        ?: ""
}