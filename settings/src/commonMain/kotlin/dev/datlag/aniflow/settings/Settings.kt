package dev.datlag.aniflow.settings

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

        suspend fun setAdultContent(value: Boolean)
    }
}