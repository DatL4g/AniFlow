package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.rating.RatingDialog
import com.maxkeppeler.sheets.rating.models.RatingBody
import com.maxkeppeler.sheets.rating.models.RatingConfig
import com.maxkeppeler.sheets.rating.models.RatingSelection
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalDI
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.ui.custom.EditFAB
import dev.datlag.aniflow.ui.navigation.screen.medium.component.*
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediumScreen(component: MediumComponent) {
    val coverImage by component.coverImage.collectAsStateWithLifecycle(component.initialMedium.coverImage)
    val dialogState by component.dialog.subscribeAsState()

    dialogState.child?.instance?.render()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val appBarState = rememberTopAppBarState()
        val scrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = appBarState
        )
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = StateSaver.List.mediaOverview,
            initialFirstVisibleItemScrollOffset = StateSaver.List.mediaOverviewOffset
        )

        Scaffold(
            modifier = Modifier.nestedScroll(scrollState.nestedScrollConnection),
            topBar = {
                CollapsingToolbar(
                    state = appBarState,
                    scrollBehavior = scrollState,
                    initialMedium = component.initialMedium,
                    titleLanguageFlow = component.titleLanguage,
                    mediumStateFlow = component.mediumState,
                    bannerImageFlow = component.bannerImage,
                    coverImage = coverImage,
                    titleFlow = component.title,
                    isFavoriteFlow = component.isFavorite,
                    isFavoriteBlockedFlow = component.isFavoriteBlocked,
                    siteUrlFlow = component.siteUrl,
                    showShare = listState.isScrollingUp(),
                    onBack = { component.back() },
                    onToggleFavorite = { component.toggleFavorite() }
                )
            },
            floatingActionButton = {
                val userRating by component.rating.collectAsStateWithLifecycle(-1)
                val ratingState = rememberUseCaseState()

                val alreadyAdded by component.alreadyAdded.collectAsStateWithLifecycle(
                    component.initialMedium.entry != null
                )
                val notReleased by component.status.mapCollect(component.initialMedium.status) {
                    it == MediaStatus.UNKNOWN__ || it == MediaStatus.NOT_YET_RELEASED
                }

                RatingDialog(
                    state = ratingState,
                    selection = RatingSelection(
                        onSelectRating = { rating, _ ->
                            component.rate(rating)
                        }
                    ),
                    header = Header.Default(
                        title = "Rate this Anime",
                        icon = IconSource(Icons.Filled.Star)
                    ),
                    body = RatingBody.Default(
                        bodyText = ""
                    ),
                    config = RatingConfig(
                        ratingOptionsCount = 5,
                        ratingOptionsSelected = userRating.takeIf { it > 0 },
                        ratingZeroValid = true
                    )
                )

                if (!notReleased) {
                    val uriHandler = LocalUriHandler.current
                    val userHelper by LocalDI.current.instance<UserHelper>()

                    EditFAB(
                        displayAdd = !alreadyAdded,
                        bsAvailable = component.bsAvailable,
                        expanded = listState.isScrollingUp(),
                        onBS = {

                        },
                        onRate = {
                            uriHandler.openUri(userHelper.loginUrl)
                        },
                        onProgress = {
                            // ratingState.show()
                        }
                    )
                }
            }
        ) {
            CompositionLocalProvider(
                LocalPaddingValues provides LocalPadding().merge(it)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = LocalPadding(top = 16.dp)
                ) {
                    item {
                        CoverSection(
                            coverImage = coverImage,
                            initialMedium = component.initialMedium,
                            formatFlow = component.format,
                            episodesFlow = component.episodes,
                            durationFlow = component.duration,
                            statusFlow = component.status,
                            modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp)
                        )
                    }
                    item {
                        RatingSection(
                            initialMedium = component.initialMedium,
                            ratedFlow = component.rated,
                            popularFlow = component.popular,
                            scoreFlow = component.score,
                            modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp)
                        )
                    }
                    item {
                        GenreSection(
                            initialMedium = component.initialMedium,
                            genreFlow = component.genres,
                            modifier = Modifier.fillParentMaxWidth()
                        )
                    }
                    item {
                        DescriptionSection(
                            initialMedium = component.initialMedium,
                            descriptionFlow = component.description,
                            translatedDescriptionFlow = component.translatedDescription,
                            modifier = Modifier.fillParentMaxWidth()
                        ) { translated ->
                            component.descriptionTranslation(translated)
                        }
                    }
                    item {
                        CharacterSection(
                            initialMedium = component.initialMedium,
                            characterFlow = component.characters,
                            charLanguage = component.charLanguage,
                            modifier = Modifier.fillParentMaxWidth().animateItemPlacement()
                        ) { char ->
                            component.showCharacter(char)
                        }
                    }
                    item {
                        TrailerSection(
                            initialMedium = component.initialMedium,
                            trailerFlow = component.trailer,
                            modifier = Modifier.fillParentMaxWidth().animateItemPlacement()
                        )
                    }
                }
            }
        }

        AdultSection(
            initialMedium = component.initialMedium,
            isAdultContentFlow = component.isAdult,
            isAdultContentAllowedFlow = component.isAdultAllowed,
            onBack = component::back
        )

        DisposableEffect(listState) {
            onDispose {
                StateSaver.List.mediaOverview = listState.firstVisibleItemIndex
                StateSaver.List.mediaOverviewOffset = listState.firstVisibleItemScrollOffset
            }
        }
    }
}