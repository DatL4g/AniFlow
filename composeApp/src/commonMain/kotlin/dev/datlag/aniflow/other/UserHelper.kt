package dev.datlag.aniflow.other

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.ViewerMutation
import dev.datlag.aniflow.anilist.ViewerQuery
import dev.datlag.aniflow.anilist.model.User
import dev.datlag.aniflow.common.toMutation
import dev.datlag.aniflow.common.toSettings
import dev.datlag.aniflow.model.safeFirstOrNull
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
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

    private val changedUser: MutableStateFlow<User?> = MutableStateFlow(null)
    private val userQuery = client.query(
        ViewerQuery(
            html = Optional.present(true)
        )
    ).toFlow()
    private val defaultUser = isLoggedIn.transform { loggedIn ->
        if (loggedIn) {
            emitAll(
                userQuery.map {
                    it.data?.Viewer?.let(::User)
                }
            )
        } else {
            emit(null)
        }
    }
    private val latestUser = defaultUser.transform { default ->
        emit(default)
        emitAll(changedUser.filterNotNull().map { changed ->
            changedUser.update { null }
            changed
        })
    }

    val user = latestUser.transform { user ->
        emit(
            user?.also {
                appSettings.setData(
                    adultContent = it.displayAdultContent,
                    color = AppSettings.Color.fromString(it.profileColor),
                    titleLanguage = it.titleLanguage.toSettings()
                )
            }
        )
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
        changedUser.emit(
            client.mutation(
                ViewerMutation(
                    adult = Optional.present(value),
                    html = Optional.present(true)
                )
            ).execute().data?.UpdateUser?.let(::User)
        )
    }

    suspend fun updateProfileColorSetting(value: AppSettings.Color?) {
        appSettings.setColor(value)

        if (value != null) {
            changedUser.emit(
                client.mutation(
                    ViewerMutation(
                        color = Optional.present(value.label),
                        html = Optional.present(true)
                    )
                ).execute().data?.UpdateUser?.let(::User)
            )
        }
    }

    suspend fun updateTitleLanguage(value: AppSettings.TitleLanguage?) {
        appSettings.setTitleLanguage(value)

        if (value != null) {
            changedUser.emit(
                client.mutation(
                    ViewerMutation(
                        title = Optional.presentIfNotNull(value.toMutation()),
                        html = Optional.present(true)
                    )
                ).execute().data?.UpdateUser?.let(::User)
            )
        }
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