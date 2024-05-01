package dev.datlag.aniflow.common

import androidx.compose.ui.graphics.Color
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.type.UserStaffNameLanguage
import dev.datlag.aniflow.anilist.type.UserTitleLanguage
import dev.icerock.moko.resources.StringResource
import dev.datlag.aniflow.settings.model.Color as SettingsColor
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar

@OptIn(ExperimentalStdlibApi::class)
fun SettingsColor.toComposeColor() = Color(
    this.hex.substringAfter('#').hexToLong() or 0x00000000FF000000
)

fun SettingsColor.toComposeString(): StringResource = when (this) {
    is SettingsColor.Blue -> SharedRes.strings.color_blue
    is SettingsColor.Purple -> SharedRes.strings.color_purple
    is SettingsColor.Pink -> SharedRes.strings.color_pink
    is SettingsColor.Orange -> SharedRes.strings.color_orange
    is SettingsColor.Red -> SharedRes.strings.color_red
    is SettingsColor.Green -> SharedRes.strings.color_green
    is SettingsColor.Gray -> SharedRes.strings.color_gray
    is SettingsColor.Custom -> SharedRes.strings.color_custom
}

fun User.TitleLanguage?.toSettings(): SettingsTitle? = when (this) {
    is User.TitleLanguage.Romaji -> SettingsTitle.Romaji
    is User.TitleLanguage.English -> SettingsTitle.English
    is User.TitleLanguage.Native -> SettingsTitle.Native
    else -> null
}

fun SettingsTitle?.toMutation(): UserTitleLanguage? = when (this) {
    is SettingsTitle.Romaji -> UserTitleLanguage.ROMAJI
    is SettingsTitle.English -> UserTitleLanguage.ENGLISH
    is SettingsTitle.Native -> UserTitleLanguage.NATIVE
    else -> null
}

fun SettingsTitle.toComposeString(): StringResource = when (this) {
    is SettingsTitle.Romaji -> SharedRes.strings.title_romaji
    is SettingsTitle.English -> SharedRes.strings.title_english
    is SettingsTitle.Native -> SharedRes.strings.title_native
}

fun User.CharLanguage?.toSettings(): SettingsChar? = when (this) {
    is User.CharLanguage.RomajiWestern -> SettingsChar.RomajiWestern
    is User.CharLanguage.Romaji -> SettingsChar.Romaji
    is User.CharLanguage.Native -> SettingsChar.Native
    else -> null
}

fun SettingsChar?.toMutation(): UserStaffNameLanguage? = when (this) {
    is SettingsChar.RomajiWestern -> UserStaffNameLanguage.ROMAJI_WESTERN
    is SettingsChar.Romaji -> UserStaffNameLanguage.ROMAJI
    is SettingsChar.Native -> UserStaffNameLanguage.NATIVE
    else -> null
}

fun SettingsChar.toComposeString(): StringResource = when (this) {
    is SettingsChar.RomajiWestern -> SharedRes.strings.char_romaji_western
    is SettingsChar.Romaji -> SharedRes.strings.char_romaji
    is SettingsChar.Native -> SharedRes.strings.char_native
}