package dev.datlag.aniflow.other

import dev.datlag.aniflow.model.saveFirstOrNull
import dev.datlag.aniflow.settings.DataStoreUserSettings
import dev.datlag.aniflow.settings.Settings
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.tokenstore.OauthTokens
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse

class TokenRefreshHandler(
    private val storeUserSettings: Settings.PlatformUserSettings
) {

    private val mutex = Mutex()

    suspend fun getAccessToken(): String? {
        return storeUserSettings.aniList.firstOrNull()?.accessToken
    }

    suspend fun refreshAndSaveToken(client: OpenIdConnectClient, oldAccessToken: String): OauthTokens {
        return refreshAndSaveToken(client::refreshToken, oldAccessToken)
    }

    suspend fun refreshAndSaveToken(refreshCall: suspend (String) -> AccessTokenResponse, oldAccessToken: String): OauthTokens {
        mutex.withLock {
            val currentTokens = storeUserSettings.aniList.saveFirstOrNull()?.let {
                OauthTokens(
                    accessToken = it.accessToken ?: return@let null,
                    refreshToken = it.refreshToken,
                    idToken = it.idToken
                )
            }

            return if (currentTokens != null && currentTokens.accessToken != oldAccessToken) {
                currentTokens
            } else {
                val refreshToken = storeUserSettings.aniListRefreshToken.firstOrNull()
                val newTokens = refreshCall(refreshToken ?: "")
                storeUserSettings.setAniListTokens(
                    access = newTokens.access_token,
                    refresh = newTokens.refresh_token,
                    id = newTokens.id_token
                )

                OauthTokens(
                    accessToken = newTokens.access_token,
                    refreshToken = newTokens.refresh_token,
                    idToken = newTokens.id_token
                )
            }
        }
    }
}