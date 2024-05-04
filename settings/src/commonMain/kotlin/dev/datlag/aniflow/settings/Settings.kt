package dev.datlag.aniflow.settings

import dev.datlag.aniflow.settings.model.*
import kotlinx.coroutines.flow.Flow

data object Settings {

    interface PlatformUserSettings {
        val aniList: Flow<UserSettings.AniList>
        val isAniListLoggedIn: Flow<Boolean>

        suspend fun setAniListAccessToken(token: String)
        suspend fun setAniListTokens(
            access: String,
            expires: Int?
        )
        suspend fun removeAniListToken()
    }

    interface PlatformAppSettings {
        val adultContent: Flow<Boolean>
        val color: Flow<Color?>
        val titleLanguage: Flow<TitleLanguage?>
        val charLanguage: Flow<CharLanguage?>
        val viewManga: Flow<Boolean>

        suspend fun setAdultContent(value: Boolean)
        suspend fun setColor(value: Color?)
        suspend fun setColor(value: String?) = setColor(value?.let {
            Color.fromString(it)
        })
        suspend fun setTitleLanguage(value: TitleLanguage?)
        suspend fun setCharLanguage(value: CharLanguage?)
        suspend fun setViewManga(value: Boolean)
        suspend fun setData(
            adultContent: Boolean,
            color: Color?,
            titleLanguage: TitleLanguage?,
            charLanguage: CharLanguage?,
        )
    }
}