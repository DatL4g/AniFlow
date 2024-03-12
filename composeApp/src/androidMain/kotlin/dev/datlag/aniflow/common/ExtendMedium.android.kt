package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery
import java.util.Locale

actual fun TrendingQuery.Medium.preferredTitle(): String {
    return this.title?.userPreferred?.ifBlank { null } ?: run {

        val locale = Locale.getDefault()
        val isJapanese = locale.language.equals("jp", ignoreCase = true)
                || locale.language.equals("ja", ignoreCase = true)
                || locale.isO3Language.equals("jpn", ignoreCase = true)

        if (isJapanese) {
            this.title?.native?.ifBlank { null }
                ?: this.title?.romaji?.ifBlank { null }
                ?: this.title?.english?.ifBlank { null }
        } else {
            this.title?.english?.ifBlank { null }
                ?: this.title?.romaji?.ifBlank { null }
                ?: this.title?.native?.ifBlank { null }
        }
    } ?: this.id.toString()
}