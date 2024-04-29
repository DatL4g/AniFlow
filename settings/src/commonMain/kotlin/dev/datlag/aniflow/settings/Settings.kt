package dev.datlag.aniflow.settings

import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.settings.model.UserSettings
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
    }

    interface PlatformAppSettings {
        val adultContent: Flow<Boolean>
        val color: Flow<AppSettings.Color?>
        val titleLanguage: Flow<AppSettings.TitleLanguage?>

        suspend fun setAdultContent(value: Boolean)
        suspend fun setColor(value: AppSettings.Color?)
        suspend fun setColor(value: String?) = setColor(value?.let {
            AppSettings.Color.fromString(it)
        })
        suspend fun setTitleLanguage(value: AppSettings.TitleLanguage?)
        suspend fun setData(
            adultContent: Boolean,
            color: AppSettings.Color?,
            titleLanguage: AppSettings.TitleLanguage?,
        )
    }
}