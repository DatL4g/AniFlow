package dev.datlag.aniflow.settings

import androidx.datastore.core.DataStore
import dev.datlag.aniflow.settings.model.UserSettings
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

class DataStoreUserSettings(
    private val dataStore: DataStore<UserSettings>
) : Settings.PlatformUserSettings {
    override val aniList: Flow<UserSettings.AniList> = dataStore.data.map { it.aniList }.distinctUntilChanged()
    private val clockNow = flow {
        do {
            emit(Clock.System.now())
            delay(1.minutes)
        } while (currentCoroutineContext().isActive)
    }
    override val isAniListLoggedIn: Flow<Boolean> = combine(
        aniList,
        clockNow
    ) { data, now ->
        data.accessToken != null && now.epochSeconds < (data.expires ?: 0)
    }.distinctUntilChanged()

    override suspend fun setAniListAccessToken(token: String) {
        dataStore.updateData {
            it.copy(
                aniList = it.aniList.copy(
                    accessToken = token
                )
            )
        }
    }

    override suspend fun setAniListTokens(
        access: String,
        expires: Int?
    ) {
        dataStore.updateData {
            it.copy(
                aniList = it.aniList.copy(
                    accessToken = access,
                    expires = expires
                )
            )
        }
    }
}