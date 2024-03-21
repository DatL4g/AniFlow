package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium

actual fun Medium.preferredTitle(): String {
    return this.title.userPreferred?.ifBlank { null }
        ?: this.title.english?.ifBlank { null }
        ?: this.title.romaji?.ifBlank { null }
        ?: this.title.native?.ifBlank { null }
        ?: this.id.toString()
}