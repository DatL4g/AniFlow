package dev.datlag.aniflow.ui.navigation.screen.nekos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.option.OptionDialog
import com.maxkeppeler.sheets.option.models.DisplayMode
import com.maxkeppeler.sheets.option.models.Option
import com.maxkeppeler.sheets.option.models.OptionConfig
import com.maxkeppeler.sheets.option.models.OptionSelection
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.nekos.NekosRepository
import dev.datlag.aniflow.nekos.model.Rating
import dev.datlag.aniflow.ui.navigation.screen.component.HidingNavigationBar
import dev.datlag.aniflow.ui.navigation.screen.component.NavigationBarState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun NekosScreen(component: NekosComponent) {
    val listState = rememberLazyStaggeredGridState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            component.back()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(text = stringResource(SharedRes.strings.nekos_api))
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                modifier = Modifier.hazeChild(
                    state = LocalHaze.current,
                    style = HazeMaterials.thin(
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                ).fillMaxWidth()
            )
        },
        floatingActionButton = {
            val dialogState = rememberUseCaseState(
                visible = false
            )
            val adultContent by component.adultContent.collectAsStateWithLifecycle(false)
            val rating by component.rating.collectAsStateWithLifecycle()

            OptionDialog(
                state = dialogState,
                config = OptionConfig(
                    mode = DisplayMode.LIST
                ),
                selection = OptionSelection.Single(
                    options = listOf(
                        Option(
                            titleText = "Safe",
                            selected = rating is Rating.Safe,
                        ),
                        Option(
                            titleText = "Suggestive",
                            selected = rating is Rating.Suggestive,
                        ),
                        Option(
                            titleText = "Borderline",
                            selected = rating is Rating.Borderline,
                            disabled = !adultContent
                        ),
                        Option(
                            titleText = "Explicit",
                            selected = rating is Rating.Explicit,
                            disabled = !adultContent
                        )
                    ),
                    onSelectOption = { option, _ ->
                        val filterRating = when (option) {
                            1 -> Rating.Suggestive
                            2 -> Rating.Borderline
                            3 -> Rating.Explicit
                            else -> Rating.Safe
                        }
                        component.filter(filterRating)
                    }
                )
            )

            ExtendedFloatingActionButton(
                onClick = { dialogState.show() },
                expanded = listState.isScrollingUp(),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = "Filter")
                }
            )
        }
    ) { padding ->
        val state by component.state.collectAsStateWithLifecycle(NekosRepository.State.None)

        when (val current = state) {
            is NekosRepository.State.None -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.2F).clip(CircleShape)
                    )
                }
            }
            is NekosRepository.State.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            component.back()
                        }
                    ) {
                        Text(text = stringResource(SharedRes.strings.back))
                    }
                }
            }
            is NekosRepository.State.Success -> {
                val uriHandler = LocalUriHandler.current

                LazyVerticalStaggeredGrid(
                    state = listState,
                    modifier = Modifier.haze(state = LocalHaze.current),
                    columns = StaggeredGridCells.Adaptive(120.dp),
                    contentPadding = padding.merge(PaddingValues(16.dp)),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    items(current.response.items, key = { it.id }) {
                        Card(
                            modifier = Modifier.animateItemPlacement(),
                            onClick = {
                                Napier.e(it.toString())
                                uriHandler.openUri(it.imageUrl ?: it.sampleUrl ?: "")
                            },
                        ) {
                            AsyncImage(
                                modifier = Modifier.fillMaxSize(),
                                model = it.imageUrl ?: it.sampleUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}