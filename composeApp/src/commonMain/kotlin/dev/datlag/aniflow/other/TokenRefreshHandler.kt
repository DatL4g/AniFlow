package dev.datlag.aniflow.other

import dev.datlag.aniflow.model.saveFirstOrNull
import dev.datlag.aniflow.settings.DataStoreUserSettings
import dev.datlag.aniflow.settings.Settings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.tokenstore.OauthTokens
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse
import kotlin.time.Duration.Companion.minutes

class TokenRefreshHandler(
    private val storeUserSettings: Settings.PlatformUserSettings
) {

    private val mutex = Mutex()
    private var lastRefresh: Int = 0

    suspend fun getAccessToken(): String? {
        return storeUserSettings.aniList.saveFirstOrNull()?.accessToken
    }

    suspend fun refreshAndSaveToken(client: OpenIdConnectClient, oldAccessToken: String): OauthTokens {
        return refreshAndSaveToken(client::refreshToken, oldAccessToken)
    }

    suspend fun refreshAndSaveToken(refreshCall: suspend (String) -> AccessTokenResponse, oldAccessToken: String): OauthTokens {
        mutex.withLock {
            val storeData = storeUserSettings.aniList.saveFirstOrNull()
            val currentTokens = storeData?.let {
                OauthTokens(
                    accessToken = it.accessToken ?: return@let null,
                    refreshToken = it.refreshToken,
                    idToken = it.idToken
                )
            }

            val nowMinus10Minutes = Clock.System.now().minus(10.minutes).epochSeconds
            val requiresRefresh = lastRefresh <= nowMinus10Minutes || nowMinus10Minutes > (storeData?.expires ?: 0)

            return if (currentTokens != null && currentTokens.accessToken != oldAccessToken && !requiresRefresh) {
                currentTokens
            } else {
                val refreshToken = storeUserSettings.aniListRefreshToken.saveFirstOrNull()
                val newTokens = refreshCall(refreshToken ?: "")
                storeUserSettings.setAniListTokens(
                    access = newTokens.access_token,
                    refresh = newTokens.refresh_token,
                    id = newTokens.id_token,
                    expires = (newTokens.expires_in ?: newTokens.refresh_token_expires_in)?.let {
                        Clock.System.now().epochSeconds + it
                    }?.toInt()
                )
                lastRefresh = Clock.System.now().epochSeconds.toInt()

                OauthTokens(
                    accessToken = newTokens.access_token,
                    refreshToken = newTokens.refresh_token,
                    idToken = newTokens.id_token
                )
            }
        }
    }
}