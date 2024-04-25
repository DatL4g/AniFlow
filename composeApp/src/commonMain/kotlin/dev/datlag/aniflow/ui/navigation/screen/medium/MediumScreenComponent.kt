package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.common.nullableFirebaseInstance
import dev.datlag.aniflow.common.onRenderApplyCommonScheme
import dev.datlag.aniflow.common.popular
import dev.datlag.aniflow.common.rated
import dev.datlag.aniflow.model.*
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.TokenRefreshHandler
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.ui.navigation.DialogComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character.CharacterDialogComponent
import dev.datlag.tooling.alsoTrue
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.withMainContext
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.safeCast
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.kodein.di.DI
import org.kodein.di.instance
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import kotlin.time.Duration.Companion.seconds

class MediumScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val initialMedium: Medium,
    private val onBack: () -> Unit
) : MediumComponent, ComponentContext by componentContext {

    private val aniListClient by di.instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT)
    private val aniListFallbackClient by di.instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT)
    private val tokenRefreshHandler by di.instance<TokenRefreshHandler>()

    private val mediumStateMachine = MediumStateMachine(
        client = aniListClient,
        fallbackClient = aniListFallbackClient,
        crashlytics = di.nullableFirebaseInstance()?.crashlytics,
        id = initialMedium.id
    )
    private val mediumState = mediumStateMachine.state.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = mediumStateMachine.currentState
    )
    private val mediumSuccessState = mediumState.mapNotNull { it.safeCast<MediumStateMachine.State.Success>() }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    override val bannerImage: StateFlow<String?> = mediumSuccessState.mapNotNull {
        it?.data?.bannerImage
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.bannerImage
    )

    override val coverImage: StateFlow<Medium.CoverImage> = mediumSuccessState.mapNotNull {
        it?.data?.coverImage
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.coverImage
    )

    override val title: StateFlow<Medium.Title> = mediumSuccessState.mapNotNull {
        it?.data?.title
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.title
    )

    override val description: StateFlow<String?> = mediumSuccessState.map {
        it?.data?.description?.ifBlank { null }
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.description
    )

    override val translatedDescription: MutableStateFlow<String?> = MutableStateFlow(null)

    override val genres: StateFlow<Set<String>> = mediumSuccessState.mapNotNull {
        it?.data?.genres?.ifEmpty { null }
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.genres
    )

    override val format: StateFlow<MediaFormat> = mediumSuccessState.mapNotNull {
        it?.data?.format
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.format
    )

    private val nextAiringEpisode: StateFlow<Medium.NextAiring?> = mediumSuccessState.mapNotNull {
        it?.data?.nextAiringEpisode
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.nextAiringEpisode
    )

    override val episodes: StateFlow<Int> = combine(
        mediumSuccessState.map {
            it?.data?.episodes
        },
        nextAiringEpisode
    ) { episodes, airing ->
        episodes.ifValueOrNull(-1) {
            if (airing != null) {
                if (Instant.fromEpochSeconds(airing.airingAt.toLong()) <= Clock.System.now()) {
                    airing.episodes
                } else {
                    airing.episodes - 1
                }
            } else {
                -1
            }
        }
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = run {
            val airing = nextAiringEpisode.value ?: return@run -1

            if (Instant.fromEpochSeconds(airing.airingAt.toLong()) <= Clock.System.now()) {
                airing.episodes
            } else {
                airing.episodes - 1
            }
        }
    )

    override val duration: StateFlow<Int> = mediumSuccessState.mapNotNull {
        it?.data?.avgEpisodeDurationInMin
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.avgEpisodeDurationInMin
    )

    override val status: StateFlow<MediaStatus> = mediumSuccessState.mapNotNull {
        it?.data?.status
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.status
    )

    override val rated: StateFlow<Medium.Ranking?> = mediumSuccessState.mapNotNull {
        it?.data?.rated()
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.rated()
    )

    override val popular: StateFlow<Medium.Ranking?> = mediumSuccessState.mapNotNull {
        it?.data?.popular()
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.popular()
    )

    override val score: StateFlow<Int?> = mediumSuccessState.mapNotNull {
        val received = it?.data?.averageScore
        if (received == null || received == -1) {
            null
        } else {
            received
        }
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = run {
            val initial = initialMedium.averageScore

            if (initial == -1) {
                null
            } else {
                initial
            }
        }
    )

    override val characters: StateFlow<Set<Character>> = mediumSuccessState.mapNotNull {
        it?.data?.characters
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptySet()
    )

    private val mediaId: StateFlow<Int> = mediumSuccessState.mapNotNull {
        it?.data?.id
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.id
    )

    private val changedRating: MutableStateFlow<Int> = MutableStateFlow(initialMedium.entry?.score?.toInt() ?: -1)
    override val rating: StateFlow<Int> = combine(
        mediumSuccessState.map {
            it?.data?.entry?.score?.toInt()
        }.flowOn(ioDispatcher()),
        changedRating
    ) { t1, t2 ->
        if (t2 > -1) {
            t2
        } else {
            t1 ?: t2
        }
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = changedRating.value
    )

    override val trailer: StateFlow<Medium.Trailer?> = mediumSuccessState.mapNotNull {
        it?.data?.trailer
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.trailer
    )

    override val alreadyAdded: StateFlow<Boolean> = mediumSuccessState.mapNotNull {
        it?.data?.entry != null
    }.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialMedium.entry != null
    )

    private val userSettings by di.instance<Settings.PlatformUserSettings>()
    private val characterStateMachine by di.instance<CharacterStateMachine>()
    private val burningSeriesResolver by di.instance<BurningSeriesResolver>()

    override val bsAvailable: Boolean
        get() = burningSeriesResolver.isAvailable

    private val dialogNavigation = SlotNavigation<DialogConfig>()
    override val dialog: Value<ChildSlot<DialogConfig, DialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer()
    ) { config, context ->
        when (config) {
            is DialogConfig.Character -> CharacterDialogComponent(
                componentContext = context,
                di = di,
                initialChar = config.initial,
                onDismiss = dialogNavigation::dismiss
            )
        }
    }

    init {
        launchIO {
            title.mapNotNull { it.english to it.romaji }.collect { (english, romaji) ->
                burningSeriesResolver.resolveByName(english = english, romaji = romaji)
            }
        }
    }

    @Composable
    override fun render() {
        val state = HazeState()

        CompositionLocalProvider(
            LocalHaze provides state
        ) {
            onRenderApplyCommonScheme(initialMedium.id) {
                MediumScreen(this)
            }
        }
    }

    override fun back() {
        onBack()
    }

    private suspend fun login(): Boolean {
        val factory by di.instance<CodeAuthFlowFactory>()
        val client by di.instance<OpenIdConnectClient>(Constants.AniList.Auth.CLIENT)
        val flow = withMainContext {
            factory.createAuthFlow(client)
        }

        val tokenResult = suspendCatching {
            flow.getAccessToken()
        }

        tokenResult.getOrNull()?.let {
            tokenRefreshHandler.updateStoredToken(it)
        }

        return tokenResult.isSuccess.alsoTrue {
            requestMediaListEntry()
        }
    }

    private suspend fun requestMediaListEntry() {
        val query = MediaListEntryQuery(
            id = Optional.present(mediaId.saveFirstOrNull() ?: mediaId.value)
        )
        val execution = CatchResult.timeout(5.seconds) {
            aniListClient.query(query).execute()
        }.asNullableSuccess()

        execution?.data?.MediaList?.let { entry ->
            changedRating.update { entry.score?.toInt() ?: it }
        }
    }

    override fun rate(onLoggedIn: () -> Unit) {
        launchIO {
            val isLoggedIn = userSettings.isAniListLoggedIn.saveFirstOrNull() ?: false

            if (!isLoggedIn) {
                if (login()) {
                    withMainContext {
                        onLoggedIn()
                    }
                }
            } else {
                val currentRating = rating.saveFirstOrNull() ?: rating.value
                if (currentRating <= -1) {
                    requestMediaListEntry()
                }

                withMainContext {
                    onLoggedIn()
                }
            }
        }
    }

    override fun rate(value: Int) {
        val mutation = RatingMutation(
            mediaId = Optional.present(mediaId.value),
            rating = Optional.present(value * 20)
        )
        launchIO {
            aniListClient.mutation(mutation).execute().data?.SaveMediaListEntry?.score?.let {
                changedRating.emit(it.toInt())
            }
        }
    }

    override fun descriptionTranslation(text: String?) {
        translatedDescription.update { text }
    }

    override fun showCharacter(character: Character) {
        dialogNavigation.activate(DialogConfig.Character(character))
    }
}