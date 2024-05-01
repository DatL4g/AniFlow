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
import dev.datlag.aniflow.model.emitNotNull
import dev.datlag.aniflow.model.mutableStateIn
import dev.datlag.aniflow.model.safeFirstOrNull
import dev.datlag.aniflow.settings.Settings
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.withIOContext
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import dev.datlag.aniflow.settings.model.Color as SettingsColor
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle
import dev.datlag.aniflow.settings.model.CharLanguage as SettingsChar

class UserHelper(
    private val userSettings: Settings.PlatformUserSettings,
    private val appSettings: Settings.PlatformAppSettings,
    private val client: ApolloClient,
    private val clientId: String
) {

    val isLoggedIn: Flow<Boolean> = userSettings.isAniListLoggedIn.distinctUntilChanged()
    val loginUrl: String = "https://anilist.co/api/v2/oauth/authorize?client_id=$clientId&response_type=token"

    @OptIn(DelicateCoroutinesApi::class)
    private val updatableUser = isLoggedIn.transform { loggedIn ->
        if (loggedIn) {
            emitAll(
                client.query(ViewerQuery()).toFlow().map {
                    it.data?.Viewer?.let(::User)
                }
            )
        } else {
            emit(null)
        }
    }.mutableStateIn(
        scope = GlobalScope,
        initialValue = null
    )

    val user = updatableUser.transform { user ->
        emit(
            user?.also {
                appSettings.setData(
                    adultContent = it.displayAdultContent,
                    color = SettingsColor.fromString(it.profileColor),
                    titleLanguage = it.titleLanguage.toSettings(),
                    charLanguage = it.charLanguage.toSettings()
                )
            }
        )
    }.flowOn(ioDispatcher())

    suspend fun updateAdultSetting(value: Boolean) {
        appSettings.setAdultContent(value)
        updatableUser.emitNotNull(
            client.mutation(
                ViewerMutation(
                    adult = Optional.present(value)
                )
            ).execute().data?.UpdateUser?.let(::User)
        )
    }

    suspend fun updateProfileColorSetting(value: SettingsColor?) {
        appSettings.setColor(value)

        if (value != null) {
            updatableUser.emitNotNull(
                client.mutation(
                    ViewerMutation(
                        color = Optional.present(value.label)
                    )
                ).execute().data?.UpdateUser?.let(::User)
            )
        }
    }

    suspend fun updateTitleLanguage(value: SettingsTitle?) {
        appSettings.setTitleLanguage(value)

        if (value != null) {
            updatableUser.emitNotNull(
                client.mutation(
                    ViewerMutation(
                        title = Optional.presentIfNotNull(value.toMutation())
                    )
                ).execute().data?.UpdateUser?.let(::User)
            )
        }
    }

    suspend fun updateCharLanguage(value: SettingsChar?) {
        appSettings.setCharLanguage(value)

        if (value != null) {
            updatableUser.emitNotNull(
                client.mutation(
                    ViewerMutation(
                        char = Optional.presentIfNotNull(value.toMutation())
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