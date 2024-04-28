package dev.datlag.aniflow.other

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.ViewerMutation
import dev.datlag.aniflow.anilist.ViewerQuery
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.model.safeFirstOrNull
import dev.datlag.aniflow.settings.Settings
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse

class UserHelper(
    private val userSettings: Settings.PlatformUserSettings,
    private val appSettings: Settings.PlatformAppSettings,
    private val client: ApolloClient,
    private val authFlowFactory: CodeAuthFlowFactory,
    private val oidc: OpenIdConnectClient
) {

    val isLoggedIn: Flow<Boolean> = userSettings.isAniListLoggedIn.distinctUntilChanged()
    val user: Flow<User?> = isLoggedIn.transform { loggedIn ->
        if (loggedIn) {
            emitAll(
                client.query(ViewerQuery()).toFlow().map {
                    it.data?.Viewer?.let(::User)?.also { user ->
                        appSettings.setAdultContent(user.displayAdultContent)
                    }
                }
            )
        } else {
            emit(null)
        }
    }

    suspend fun login(): Boolean {
        if (isLoggedIn.safeFirstOrNull() == true) {
            return true
        }

        val flow = withMainContext {
            authFlowFactory.createAuthFlow(oidc)
        }

        val tokenResult = suspendCatching {
            flow.getAccessToken()
        }

        tokenResult.getOrNull()?.let {
            updateStoredToken(it)
        }

        return tokenResult.isSuccess
    }

    suspend fun updateAdultSetting(value: Boolean) {
        appSettings.setAdultContent(value)
        client.mutation(
            ViewerMutation(
                adult = Optional.present(value)
            )
        ).execute()
    }

    private suspend fun updateStoredToken(tokenResponse: AccessTokenResponse) {
        userSettings.setAniListTokens(
            access = tokenResponse.access_token,
            expires = tokenResponse.expires_in?.let {
                Clock.System.now().epochSeconds + it
            }?.toInt()
        )
    }
}