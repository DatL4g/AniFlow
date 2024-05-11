package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
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
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.model.*
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.settings.model.CharLanguage
import dev.datlag.aniflow.ui.navigation.DialogComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character.CharacterDialogComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit.EditDialogComponent
import dev.datlag.tooling.alsoTrue
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.withMainContext
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.safeCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.time.Duration.Companion.seconds
import dev.datlag.aniflow.settings.model.TitleLanguage as SettingsTitle

class MediumScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val initialMedium: Medium,
    private val onBack: () -> Unit
) : MediumComponent, ComponentContext by componentContext {

    private val aniListClient by di.instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT)
    private val appSettings by di.instance<Settings.PlatformAppSettings>()

    private val userHelper by di.instance<UserHelper>()
    override val isLoggedIn: Flow<Boolean> = userHelper.isLoggedIn
    override val loginUri: String = userHelper.loginUrl

    override val titleLanguage: Flow<SettingsTitle?> = appSettings.titleLanguage.flowOn(ioDispatcher())
    override val charLanguage: Flow<CharLanguage?> = appSettings.charLanguage.flowOn(ioDispatcher())

    private val mediumRepository by di.instance<MediumRepository>()
    override val mediumState = mediumRepository.medium

    private val mediumSuccessState = mediumState.mapNotNull {
        it.safeCast<MediumRepository.State.Success>()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isAdult: Flow<Boolean> = mediumSuccessState.mapLatest {
        it.medium.isAdult
    }

    override val isAdultAllowed: Flow<Boolean> = appSettings.adultContent

    @OptIn(ExperimentalCoroutinesApi::class)
    override val type: Flow<MediaType> = mediumSuccessState.mapLatest {
        it.medium.type
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val bannerImage: Flow<String?> = mediumSuccessState.mapLatest {
        it.medium.bannerImage
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val coverImage: Flow<Medium.CoverImage> = mediumSuccessState.mapLatest {
        it.medium.coverImage
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val title: Flow<Medium.Title> = mediumSuccessState.mapLatest {
        it.medium.title
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val description: Flow<String?> = mediumSuccessState.mapLatest {
        it.medium.description?.ifBlank { null }
    }

    override val translatedDescription: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val genres: Flow<Set<String>> = mediumSuccessState.mapLatest {
        it.medium.genres
    }.mapNotEmpty()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val format: Flow<MediaFormat> = mediumSuccessState.mapLatest {
        it.medium.format
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val episodes: Flow<Int> = mediumSuccessState.mapLatest {
        it.medium.episodes
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val duration: Flow<Int> = mediumSuccessState.mapLatest {
        it.medium.avgEpisodeDurationInMin
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val status: Flow<MediaStatus> = mediumSuccessState.mapLatest {
        it.medium.status
    }.distinctUntilChanged()

    override val rated: Flow<Medium.Ranking?> = mediumSuccessState.mapNotNull {
        it.medium.rated()
    }.distinctUntilChanged()

    override val popular: Flow<Medium.Ranking?> = mediumSuccessState.mapNotNull {
        it.medium.popular()
    }.distinctUntilChanged()

    override val score: Flow<Int?> = mediumSuccessState.mapNotNull {
        it.medium.averageScore.asNullIf(-1)
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val characters: Flow<Set<Character>> = mediumSuccessState.mapLatest {
        it.medium.characters
    }.mapNotEmpty()

    private val changedRating: MutableStateFlow<Int> = MutableStateFlow(initialMedium.entry?.score?.toInt() ?: -1)
    @OptIn(ExperimentalCoroutinesApi::class)
    override val rating: Flow<Int> = combine(
        mediumSuccessState.mapLatest {
            it.medium.entry?.score?.toInt()
        },
        changedRating
    ) { t1, t2 ->
        if (t2 > -1) {
            t2
        } else {
            t1 ?: t2
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val trailer: Flow<Medium.Trailer?> = mediumSuccessState.mapLatest {
        it.medium.trailer
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val alreadyAdded: Flow<Boolean> = mediumSuccessState.mapLatest {
        it.medium.entry != null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isFavorite: Flow<Boolean> = mediumSuccessState.mapLatest {
        it.medium.isFavorite
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val isFavoriteBlocked: Flow<Boolean> = mediumSuccessState.mapLatest {
        it.medium.isFavoriteBlocked
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val siteUrl: Flow<String> = mediumSuccessState.mapLatest {
        it.medium.siteUrl
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val listStatus: Flow<MediaListStatus> = mediumSuccessState.mapLatest {
        it.medium.entry?.status ?: MediaListStatus.UNKNOWN__
    }

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
            is DialogConfig.Edit -> EditDialogComponent(
                componentContext = context,
                di = di,
                titleFlow = title,
                onDismiss = dialogNavigation::dismiss
            )
        }
    }

    init {
        mediumRepository.load(initialMedium.id)
    }

    @Composable
    override fun render() {
        val state = remember { HazeState() }

        CompositionLocalProvider(
            LocalHaze provides state
        ) {
            onRenderWithScheme(initialMedium.id) {
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
            val currentRating = rating.safeFirstOrNull() ?: initialMedium.entry?.score?.toInt() ?: -1
            if (currentRating <= -1) {
                requestMediaListEntry()
            }

            withMainContext {
                onLoggedIn()
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

    override fun edit() {
        dialogNavigation.activate(DialogConfig.Edit)
    }
}