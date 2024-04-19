package dev.datlag.aniflow.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class DataStoreUserSettings(
    private val dataStore: DataStore<UserSettings>
) : Settings.PlatformUserSettings {
    override val aniList: Flow<UserSettings.AniList> = dataStore.data.map { it.aniList }
    override val aniListRefreshToken: Flow<String?> = aniList.map { it.refreshToken }
    override val isAniListLoggedIn: Flow<Boolean> = aniList.map {
        it.accessToken != null && Clock.System.now().epochSeconds < (it.expires ?: 0)
    }

    override suspend fun setAniListAccessToken(token: String) {
        dataStore.updateData {
            it.copy(
                aniList = it.aniList.copy(
                    accessToken = token
                )
            )
        }
    }

    override suspend fun setAniListRefreshToken(token: String) {
        dataStore.updateData {
            it.copy(
                aniList = it.aniList.copy(
                    refreshToken = token
                )
            )
        }
    }

    override suspend fun setAniListIdToken(token: String) {
        dataStore.updateData {
            it.copy(
                aniList = it.aniList.copy(
                    idToken = token
                )
            )
        }
    }

    override suspend fun setAniListTokens(
        access: String,
        refresh: String?,
        id: String?,
        expires: Int?
    ) {
        dataStore.updateData {
            it.copy(
                aniList = it.aniList.copy(
                    accessToken = access,
                    refreshToken = refresh,
                    idToken = id,
                    expires = expires
                )
            )
        }
    }
}