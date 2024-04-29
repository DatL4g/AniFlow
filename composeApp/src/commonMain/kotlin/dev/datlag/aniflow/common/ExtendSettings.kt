package dev.datlag.aniflow.common

import androidx.compose.ui.graphics.Color
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.anilist.type.UserTitleLanguage
import dev.icerock.moko.resources.StringResource
import dev.datlag.aniflow.settings.model.AppSettings

@OptIn(ExperimentalStdlibApi::class)
fun AppSettings.Color.toComposeColor() = Color(
    this.hex.substringAfter('#').hexToLong() or 0x00000000FF000000
)

fun AppSettings.Color.toComposeString(): StringResource = when (this) {
    is AppSettings.Color.Blue -> SharedRes.strings.color_blue
    is AppSettings.Color.Purple -> SharedRes.strings.color_purple
    is AppSettings.Color.Pink -> SharedRes.strings.color_pink
    is AppSettings.Color.Orange -> SharedRes.strings.color_orange
    is AppSettings.Color.Red -> SharedRes.strings.color_red
    is AppSettings.Color.Green -> SharedRes.strings.color_green
    is AppSettings.Color.Gray -> SharedRes.strings.color_gray
    is AppSettings.Color.Custom -> SharedRes.strings.color_custom
}

fun User.TitleLanguage?.toSettings(): AppSettings.TitleLanguage? = when (this) {
    is User.TitleLanguage.Romaji -> AppSettings.TitleLanguage.Romaji
    is User.TitleLanguage.English -> AppSettings.TitleLanguage.English
    is User.TitleLanguage.Native -> AppSettings.TitleLanguage.Native
    else -> null
}

fun AppSettings.TitleLanguage?.toMutation(): UserTitleLanguage? = when (this) {
    is AppSettings.TitleLanguage.Romaji -> UserTitleLanguage.ROMAJI
    is AppSettings.TitleLanguage.English -> UserTitleLanguage.ENGLISH
    is AppSettings.TitleLanguage.Native -> UserTitleLanguage.NATIVE
    else -> null
}

fun AppSettings.TitleLanguage.toComposeString(): StringResource = when (this) {
    is AppSettings.TitleLanguage.Romaji -> SharedRes.strings.title_romaji
    is AppSettings.TitleLanguage.English -> SharedRes.strings.title_english
    is AppSettings.TitleLanguage.Native -> SharedRes.strings.title_native
}