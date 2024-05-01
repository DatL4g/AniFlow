package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.TrendingQuery
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar
import dev.datlag.aniflow.model.appendWithSpace

actual fun Medium.Title.preferred(setting: SettingsTitle?): String {
    if (setting != null) {
        return when (setting) {
            is SettingsTitle.Romaji -> this.romaji?.ifBlank { null }
                ?: this.english?.ifBlank { null }
                ?: this.native?.ifBlank { null }
                ?: ""
            is SettingsTitle.English -> this.english?.ifBlank { null }
                ?: this.romaji?.ifBlank { null }
                ?: this.native?.ifBlank { null }
                ?: ""
            is SettingsTitle.Native -> this.native?.ifBlank { null }
                ?: this.romaji?.ifBlank { null }
                ?: this.english?.ifBlank { null }
                ?: ""
        }
    }

    return this.userPreferred?.ifBlank { null }
        ?: this.english?.ifBlank { null }
        ?: this.romaji?.ifBlank { null }
        ?: this.native?.ifBlank { null }
        ?: ""
}

actual fun Character.Name.preferred(setting: SettingsChar?): String {
    if (setting != null) {
        return when (setting) {
            is SettingsChar.RomajiWestern -> buildString {
                appendWithSpace(this@preferred.first)
                appendWithSpace(this@preferred.middle)
                appendWithSpace(this@preferred.last)
            }.trim().ifBlank { null }
                ?: this.userPreferred?.ifBlank { null }
                ?: this.full?.ifBlank { null }
                ?: this.native?.ifBlank { null }
                ?: ""

            is SettingsChar.Romaji -> buildString {
                appendWithSpace(this@preferred.last)
                appendWithSpace(this@preferred.middle)
                appendWithSpace(this@preferred.first)
            }.trim().ifBlank { null }
                ?: this.userPreferred?.ifBlank { null }
                ?: this.full?.ifBlank { null }
                ?: this.native?.ifBlank { null }
                ?: ""

            is SettingsChar.Native -> this.native?.ifBlank { null }
                ?: this.userPreferred?.ifBlank { null }
                ?: buildString {
                    appendWithSpace(this@preferred.last)
                    appendWithSpace(this@preferred.middle)
                    appendWithSpace(this@preferred.first)
                }.trim().ifBlank { null }
                ?: this.full?.ifBlank { null }
                ?: ""
        }
    }

    return this.userPreferred?.ifBlank { null }
        ?: this.full?.ifBlank { null }
        ?: buildString {
            appendWithSpace(this@preferred.first)
            appendWithSpace(this@preferred.middle)
            appendWithSpace(this@preferred.last)
        }.trim().ifBlank { null }
        ?: this.native?.ifBlank { null }
        ?: ""
}