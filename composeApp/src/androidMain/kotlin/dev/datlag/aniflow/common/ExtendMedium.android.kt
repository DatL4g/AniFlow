package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import java.util.Locale

actual fun Medium.Title.preferred(): String {
    return this.userPreferred?.ifBlank { null } ?: run {

        val locale = Locale.getDefault()
        val isJapanese = locale.language.equals("jp", ignoreCase = true)
                || locale.language.equals("ja", ignoreCase = true)
                || locale.isO3Language.equals("jpn", ignoreCase = true)

        if (isJapanese) {
            this.native?.ifBlank { null }
                ?: this.romaji?.ifBlank { null }
                ?: this.english?.ifBlank { null }
        } else {
            this.english?.ifBlank { null }
                ?: this.romaji?.ifBlank { null }
                ?: this.native?.ifBlank { null }
        }
    } ?: ""
}

actual fun Medium.Character.Name.preferred(): String {
    return this.userPreferred?.ifBlank { null } ?: run {

        val locale = Locale.getDefault()
        val isJapanese = locale.language.equals("jp", ignoreCase = true)
                || locale.language.equals("ja", ignoreCase = true)
                || locale.isO3Language.equals("jpn", ignoreCase = true)

        if (isJapanese) {
            this.native?.ifBlank { null }
                ?: this.full?.ifBlank { null }
                ?: buildString {
                    append(this@preferred.first)
                    append(" ")
                    append(this@preferred.middle)
                    append(" ")
                    append(this@preferred.last)
                }.ifBlank { null }
        } else {
            this.full?.ifBlank { null }
                ?: buildString {
                    append(this@preferred.first)
                    append(" ")
                    append(this@preferred.middle)
                    append(" ")
                    append(this@preferred.last)
                }.ifBlank { null }
                ?: this.native
        }
    } ?: ""
}