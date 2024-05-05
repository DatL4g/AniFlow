package dev.datlag.aniflow.ui.navigation.screen.initial.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialComponent
import dev.datlag.aniflow.ui.navigation.screen.initial.home.component.CollapsingToolbar
import dev.datlag.aniflow.ui.navigation.screen.initial.model.FABConfig
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CompactScreen(component: InitialComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingToolbar(
                state = appBarState,
                scrollBehavior = scrollBehavior,
                viewTypeFlow = component.viewing,
                onProfileClick = {

                },
                onAnimeClick = {
                    component.viewAnime()
                },
                onMangaClick = {
                    component.viewManga()
                }
            )
        },
        bottomBar = {
            val selectedPage by component.selectedPage.subscribeAsState()

            NavigationBar(
                modifier = Modifier.hazeChild(
                    state = LocalHaze.current,
                    style = HazeMaterials.thin(NavigationBarDefaults.containerColor)
                ).fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.contentColorFor(NavigationBarDefaults.containerColor)
            ) {
                component.pagerItems.forEachIndexed { index, pagerItem ->
                    NavigationBarItem(
                        selected = selectedPage == index,
                        icon = {
                            NavIcon(pagerItem)
                        },
                        onClick = {
                            component.selectPage(index)
                        },
                        label = {
                            Text(text = stringResource(pagerItem.label))
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        },
        floatingActionButton = {
            val state by FABConfig.state

            when (val current = state) {
                is FABConfig.Scan -> {
                    if (!current.loading) {
                        ExtendedFloatingActionButton(
                            onClick = current.onClick,
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.CameraEnhance,
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text(
                                    text = "Scan"
                                )
                            },
                            expanded = current.listState.isScrollingUp(),
                        )
                    }
                }
                else -> { }
            }
        }
    ) {
        CompositionLocalProvider(
            LocalPaddingValues provides it
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val selectedPage by component.selectedPage.subscribeAsState()

                Pages(
                    pages = component.pages,
                    onPageSelected = { index ->
                        if (selectedPage != index) {
                            component.selectPage(index)
                        }
                    },
                    pager = { modifier, pagerState, key, pageContent ->
                        HorizontalPager(
                            modifier = modifier,
                            state = pagerState,
                            key = key,
                            pageContent = pageContent,
                            userScrollEnabled = false
                        )
                    }
                ) { _, page ->
                    page.render()
                }
            }
        }
    }
}