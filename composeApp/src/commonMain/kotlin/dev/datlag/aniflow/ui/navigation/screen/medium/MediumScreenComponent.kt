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
import dev.datlag.aniflow.settings.model.AppSettings
import dev.datlag.aniflow.settings.model.CharLanguage
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

    override val titleLanguage: Flow<SettingsTitle?> = appSettings.titleLanguage.flowOn(ioDispatcher())
    override val charLanguage: Flow<CharLanguage?> = appSettings.charLanguage.flowOn(ioDispatcher())

    private val mediumRepository by di.instance<MediumRepository>()
    override val mediumState = mediumRepository.medium

    private val mediumSuccessState = mediumState.mapNotNull {
        it.safeCast<MediumRepository.State.Success>()
    }

    override val isAdult: Flow<Boolean> = mediumSuccessState.map {
        it.medium.isAdult
    }

    override val isAdultAllowed: Flow<Boolean> = appSettings.adultContent

    private val type: Flow<MediaType> = mediumSuccessState.map {
        it.medium.type
    }

    override val bannerImage: Flow<String?> = mediumSuccessState.map {
        it.medium.bannerImage
    }

    override val coverImage: Flow<Medium.CoverImage> = mediumSuccessState.map {
        it.medium.coverImage
    }

    override val title: Flow<Medium.Title> = mediumSuccessState.map {
        it.medium.title
    }

    override val description: Flow<String?> = mediumSuccessState.map {
        it.medium.description?.ifBlank { null }
    }

    override val translatedDescription: MutableStateFlow<String?> = MutableStateFlow(null)

    override val genres: Flow<Set<String>> = mediumSuccessState.map {
        it.medium.genres
    }.mapNotEmpty()

    override val format: Flow<MediaFormat> = mediumSuccessState.map {
        it.medium.format
    }

    override val episodes: Flow<Int> = mediumSuccessState.map {
        it.medium.episodes
    }.distinctUntilChanged()

    override val duration: Flow<Int> = mediumSuccessState.map {
        it.medium.avgEpisodeDurationInMin
    }.distinctUntilChanged()

    override val status: Flow<MediaStatus> = mediumSuccessState.map {
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

    override val characters: Flow<Set<Character>> = mediumSuccessState.map {
        it.medium.characters
    }.mapNotEmpty()

    private val changedRating: MutableStateFlow<Int> = MutableStateFlow(initialMedium.entry?.score?.toInt() ?: -1)
    override val rating: Flow<Int> = combine(
        mediumSuccessState.map {
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

    override val trailer: Flow<Medium.Trailer?> = mediumSuccessState.map {
        it.medium.trailer
    }

    override val alreadyAdded: Flow<Boolean> = mediumSuccessState.map {
        it.medium.entry != null
    }

    override val isFavorite: Flow<Boolean> = mediumSuccessState.map {
        it.medium.isFavorite
    }

    override val isFavoriteBlocked: Flow<Boolean> = mediumSuccessState.map {
        it.medium.isFavoriteBlocked
    }

    override val siteUrl: Flow<String> = mediumSuccessState.map {
        it.medium.siteUrl
    }

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
        mediumRepository.load(initialMedium.id)
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
}