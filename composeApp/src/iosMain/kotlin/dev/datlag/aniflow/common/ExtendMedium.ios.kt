package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery

actual fun TrendingQuery.Medium.preferredTitle(): String {
    return this.title?.userPreferred?.ifBlank { null }
        ?: this.title?.english?.ifBlank { null }
        ?: this.title?.romaji?.ifBlank { null }
        ?: this.title?.native?.ifBlank { null }
        ?: this.id.toString()
}