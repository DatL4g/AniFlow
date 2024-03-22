package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium

actual fun Medium.Title.preferred(): String {
    return this.userPreferred?.ifBlank { null }
        ?: this.english?.ifBlank { null }
        ?: this.romaji?.ifBlank { null }
        ?: this.native?.ifBlank { null }
        ?: ""
}