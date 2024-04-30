package dev.datlag.aniflow.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
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
import dev.datlag.tooling.compose.withIOContext
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class UserHelper(
    private val userSettings: Settings.PlatformUserSettings,
    private val appSettings: Settings.PlatformAppSettings,
    private val client: ApolloClient,
    private val clientId: String
) {

    val isLoggedIn: Flow<Boolean> = userSettings.isAniListLoggedIn.distinctUntilChanged()
    val loginUrl: String = "https://anilist.co/api/v2/oauth/authorize?client_id=$clientId&response_type=token"

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

    suspend fun saveLogin(
        accessToken: String,
        expiresIn: Int?,
    ) {
        userSettings.setAniListTokens(
            access = accessToken,
            expires = expiresIn?.let {
                Clock.System.now().plus(it.seconds).epochSeconds.toInt()
            }
        )
    }
}