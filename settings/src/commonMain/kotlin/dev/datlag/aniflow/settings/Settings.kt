package dev.datlag.aniflow.settings

import kotlinx.coroutines.flow.Flow

data object Settings {

    interface PlatformUserSettings {
        val aniList: Flow<UserSettings.AniList>
        val aniListRefreshToken: Flow<String?>
        val isAniListLoggedIn: Flow<Boolean>

        suspend fun setAniListAccessToken(token: String)
        suspend fun setAniListRefreshToken(token: String)
        suspend fun setAniListIdToken(token: String)
        suspend fun setAniListTokens(access: String, refresh: String?, id: String?)
    }
}