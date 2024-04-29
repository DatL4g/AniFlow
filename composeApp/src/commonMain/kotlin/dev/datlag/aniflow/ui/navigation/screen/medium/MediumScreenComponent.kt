package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import dev.chrisbanes.haze.HazeState
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.nullableFirebaseInstance
import dev.datlag.aniflow.common.onRenderApplyCommonScheme
import dev.datlag.aniflow.common.popular
import dev.datlag.aniflow.common.rated
import dev.datlag.aniflow.model.*
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.ui.navigation.DialogComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character.CharacterDialogComponent
import dev.datlag.tooling.alsoTrue
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.withMainContext
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.safeCast
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
    private val appSettings by di.instance<Settings.PlatformAppSettings>()
    private val userHelper by di.instance<UserHelper>()

    private val mediumStateMachine = MediumStateMachine(
        client = aniListClient,
        fallbackClient = aniListFallbackClient,
        crashlytics = di.nullableFirebaseInstance()?.crashlytics,
        id = initialMedium.id
    )

    override val mediumState = mediumStateMachine.state.flowOn(
        context = ioDispatcher()
    ).shareIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed()
    )

    override val initialState: MediumStateMachine.State
        get() = mediumStateMachine.currentState

    private val mediumSuccessState = mediumState.mapNotNull {
        it.safeCast<MediumStateMachine.State.Success>()
    }.flowOn(
        context = ioDispatcher()
    ).shareIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed()
    )

    override val isAdult: Flow<Boolean> = mediumSuccessState.mapNotNull {
        it.data.isAdult
    }.flowOn(
        context = ioDispatcher()
    )

    override val isAdultAllowed: Flow<Boolean> = appSettings.adultContent

    private val type: Flow<MediaType> = mediumSuccessState.mapNotNull {
        it.data.type
    }.flowOn(
        context = ioDispatcher()
    )

    override val bannerImage: Flow<String?> = mediumSuccessState.mapNotNull {
        it.data.bannerImage
    }.flowOn(
        context = ioDispatcher()
    )

    override val coverImage: Flow<Medium.CoverImage> = mediumSuccessState.mapNotNull {
        it.data.coverImage
    }.flowOn(
        context = ioDispatcher()
    )

    override val title: Flow<Medium.Title> = mediumSuccessState.mapNotNull {
        it.data.title
    }.flowOn(
        context = ioDispatcher()
    )

    override val description: Flow<String?> = mediumSuccessState.map {
        it.data.description?.ifBlank { null }
    }.flowOn(
        context = ioDispatcher()
    )

    override val translatedDescription: MutableStateFlow<String?> = MutableStateFlow(null)

    override val genres: Flow<Set<String>> = mediumSuccessState.mapNotNull {
        it.data.genres.ifEmpty { null }
    }.flowOn(
        context = ioDispatcher()
    )

    override val format: Flow<MediaFormat> = mediumSuccessState.mapNotNull {
        it.data.format
    }.flowOn(
        context = ioDispatcher()
    )

    private val nextAiringEpisode: Flow<Medium.NextAiring?> = mediumSuccessState.mapNotNull {
        it.data.nextAiringEpisode
    }.flowOn(
        context = ioDispatcher()
    )

    override val episodes: Flow<Int> = combine(
        mediumSuccessState.map {
            it.data.episodes
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
    )

    override val duration: Flow<Int> = mediumSuccessState.mapNotNull {
        it.data.avgEpisodeDurationInMin
    }.flowOn(
        context = ioDispatcher()
    )

    override val status: Flow<MediaStatus> = mediumSuccessState.mapNotNull {
        it.data.status
    }.flowOn(
        context = ioDispatcher()
    )

    override val rated: Flow<Medium.Ranking?> = mediumSuccessState.mapNotNull {
        it.data.rated()
    }.flowOn(
        context = ioDispatcher()
    )

    override val popular: Flow<Medium.Ranking?> = mediumSuccessState.mapNotNull {
        it.data.popular()
    }.flowOn(
        context = ioDispatcher()
    )

    override val score: Flow<Int?> = mediumSuccessState.mapNotNull {
        it.data.averageScore.ifValue(-1) { return@mapNotNull null }
    }.flowOn(
        context = ioDispatcher()
    )

    override val characters: Flow<Set<Character>> = mediumSuccessState.mapNotNull {
        it.data.characters
    }.flowOn(
        context = ioDispatcher()
    )

    private val mediaId: Flow<Int> = mediumSuccessState.mapNotNull {
        it.data.id
    }.flowOn(
        context = ioDispatcher()
    )

    private val changedRating: MutableStateFlow<Int> = MutableStateFlow(initialMedium.entry?.score?.toInt() ?: -1)
    override val rating: Flow<Int> = combine(
        mediumSuccessState.map {
            it.data.entry?.score?.toInt()
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
    )

    override val trailer: Flow<Medium.Trailer?> = mediumSuccessState.mapNotNull {
        it.data.trailer
    }.flowOn(
        context = ioDispatcher()
    )

    override val alreadyAdded: Flow<Boolean> = mediumSuccessState.mapNotNull {
        it.data.entry != null
    }.flowOn(
        context = ioDispatcher()
    )

    override val isFavorite: Flow<Boolean> = mediumSuccessState.mapNotNull {
        it.data.isFavorite
    }.flowOn(
        context = ioDispatcher()
    )

    override val isFavoriteBlocked: Flow<Boolean> = mediumSuccessState.mapNotNull {
        it.data.isFavoriteBlocked
    }.flowOn(
        context = ioDispatcher()
    )

    override val siteUrl: Flow<String> = mediumSuccessState.mapNotNull {
        it.data.siteUrl
    }.flowOn(
        context = ioDispatcher()
    )

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

    private suspend fun requestMediaListEntry() {
        val query = MediaListEntryQuery(
            id = Optional.present(initialMedium.id)
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
            if (userHelper.login()) {
                val currentRating = rating.safeFirstOrNull() ?: initialMedium.entry?.score?.toInt() ?: -1
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
            mediaId = Optional.present(initialMedium.id),
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

    override fun toggleFavorite() {
        launchIO {
            val mediaType = type.safeFirstOrNull() ?: initialMedium.type
            if (mediaType == MediaType.UNKNOWN__) {
                return@launchIO
            }

            val id = initialMedium.id
            val mutation = FavoriteToggleMutation(
                animeId = if (mediaType == MediaType.ANIME) {
                    Optional.present(id)
                } else {
                    Optional.absent()
                },
                mangaId = if (mediaType == MediaType.MANGA) {
                    Optional.present(id)
                } else {
                    Optional.absent()
                }
            )

            aniListClient.mutation(mutation).execute()
        }
    }
}