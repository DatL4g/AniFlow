package dev.datlag.aniflow.ui.navigation.screen.medium

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.GetApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
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
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.MediumRepository
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.common.*
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.ui.custom.InstantAppContent
import dev.datlag.aniflow.ui.navigation.screen.medium.component.*
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
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
        val (index, offset) = StateSaver.List.mediumOverview(component.initialMedium.id)
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = index,
            initialFirstVisibleItemScrollOffset = offset
        )

        Scaffold(
            modifier = Modifier.nestedScroll(scrollState.nestedScrollConnection),
            topBar = {
                CollapsingToolbar(
                    state = appBarState,
                    scrollBehavior = scrollState,
                    coverImage = coverImage,
                    showShare = listState.scrollUpVisible(),
                    component = component
                )
            },
            floatingActionButton = {
                FABContent(
                    expanded = listState.scrollUpVisible(),
                    component = component
                )
            }
        ) { padding ->
            val smoothPadding by animatePaddingAsState(padding)
            val mediumState by component.mediumState.collectAsStateWithLifecycle(null)
            val isLoading = remember(mediumState) {
                mediumState == null || mediumState is MediumRepository.State.None
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().haze(state = LocalHaze.current),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = smoothPadding.plus(PaddingValues(top = 16.dp))
            ) {
                item {
                    CoverSection(
                        coverImage = coverImage,
                        component = component,
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
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(fraction = 0.2F).clip(CircleShape)
                            )
                        }
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
                StateSaver.List.mediumOverview(
                    id = component.initialMedium.id,
                    index = listState.firstVisibleItemIndex,
                    offset = listState.firstVisibleItemScrollOffset
                )
            }
        }
    }
}