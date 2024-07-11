package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
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
import dev.datlag.aniflow.other.Series
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.settings.model.CharLanguage
import dev.datlag.aniflow.ui.navigation.DialogComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.character.CharacterDialogComponent
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit.EditDialogComponent
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.safeCast
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance
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
    override val mediumState = mediumRepository.medium.flowOn(
        context = ioDispatcher()
    ).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = MediumRepository.State.None
    )

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
    override val genres: Flow<ImmutableSet<String>> = mediumSuccessState.mapLatest {
        it.medium.genres
    }.mapNotEmpty()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val format: Flow<MediaFormat> = mediumSuccessState.mapLatest {
        it.medium.format
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val episodesOrChapters: Flow<Int> = mediumSuccessState.mapLatest {
        it.medium.episodesOrChapters
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
    override val characters: Flow<ImmutableSet<Character>> = mediumSuccessState.mapLatest {
        it.medium.characters
    }.mapNotEmpty()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val rating: MutableStateFlow<Int> = mediumSuccessState.mapNotNull {
        it.medium.entry?.score?.toInt()
    }.mutableStateIn(
        scope = ioScope(),
        initialValue = initialMedium.entry?.score?.toInt() ?: -1
    )

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
    override val listStatus: MutableStateFlow<MediaListStatus> = mediumSuccessState.mapLatest {
        it.medium.entry?.status ?: MediaListStatus.UNKNOWN__
    }.mutableStateIn(
        scope = ioScope(),
        initialValue = initialMedium.entry?.status ?: MediaListStatus.UNKNOWN__
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val volumes: Flow<Int> = mediumSuccessState.mapLatest {
        it.medium.volumes
    }

    private val burningSeriesResolver by instance<BurningSeriesResolver>()

    override val bsAvailable: Boolean
        get() = burningSeriesResolver.isAvailable

    override val bsVersionCode: Int
        get() = burningSeriesResolver.versionCode

    override val bsVersionName: String?
        get() = burningSeriesResolver.versionName

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bsDefaultOptions = title.mapLatest {
        burningSeriesResolver.resolveByName(it.english, it.romaji)
    }.flowOn(ioDispatcher()).stateIn(
        scope = ioScope(),
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptySet()
    )

    private val bsSearch = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bsSearchOptions = bsSearch.mapLatest {
        it?.ifBlank { null }?.let(burningSeriesResolver::resolveByName).orEmpty()
    }.flowOn(ioDispatcher())

    override val bsOptions: Flow<ImmutableCollection<Series>> = combine(
        bsSearchOptions,
        bsDefaultOptions
    ) { search, default ->
        search.ifEmpty { default }.toImmutableSet()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val watchProgress = mediumSuccessState.mapLatest {
        it.medium.entry?.progress
    }.mutableStateIn(
        scope = ioScope(),
        initialValue = initialMedium.entry?.progress
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val watchRepeat = mediumSuccessState.mapLatest {
        it.medium.entry?.repeatCount
    }.mutableStateIn(
        scope = ioScope(),
        initialValue = initialMedium.entry?.repeatCount
    )

    private val dialogNavigation = SlotNavigation<DialogConfig>()
    @OptIn(ExperimentalCoroutinesApi::class)
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
                episodesOrChapters = episodesOrChapters,
                progress = config.watched?.let(::flowOf) ?: watchProgress,
                listStatus = listStatus,
                repeatCount = watchRepeat,
                episodeStartDate = mediumSuccessState.mapLatest {
                    it.medium.startDate
                },
                type = type,
                onDismiss = dialogNavigation::dismiss,
                onSave = { status, progress, repeat ->
                    dialogNavigation.dismiss {
                        editSave(status, progress, repeat)
                    }
                }
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

    override fun rate(value: Int) {
        val newRating = mediumRepository
            .updateRatingCall(value * 20)
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .toFlow()
            .mapNotNull {
                it.data?.SaveMediaListEntry?.score?.toInt()
            }

        launchIO {
            rating.emitAll(newRating)
        }
    }

    private fun editSave(
        state: MediaListStatus,
        progress: Int,
        repeat: Int,
    ) {
        val newData = mediumRepository
            .updateEditCall(
                status = state,
                progress = progress,
                repeat = repeat
            )
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .toFlow()
            .mapNotNull {
                it.data?.SaveMediaListEntry
            }

        launchIO {
            newData.collect { data ->
                data.status?.let { listStatus.emit(it) }
                data.progress?.let { watchProgress.emit(it) }
                data.repeat?.let { watchRepeat.emit(it) }
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
        dialogNavigation.activate(DialogConfig.Edit())
    }

    override suspend fun searchBS(value: String) {
        bsSearch.update { value }
    }

    override fun selectBS(series: Series) {
        val max = burningSeriesResolver.resolveWatchedEpisode(series.href)

        dialogNavigation.activate(DialogConfig.Edit(max))
    }
}