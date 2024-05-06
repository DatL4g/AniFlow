package dev.datlag.aniflow.ui.navigation.screen.initial.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.ui.custom.ExpandedPages
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialComponent
import dev.datlag.aniflow.ui.navigation.screen.component.CollapsingToolbar
import dev.datlag.aniflow.ui.navigation.screen.initial.model.FABConfig
import dev.datlag.tooling.compose.EndCornerShape
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpandedScreen(component: InitialComponent) {
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
                    component.viewProfile()
                },
                onAnimeClick = {
                    component.viewAnime()
                },
                onMangaClick = {
                    component.viewManga()
                }
            )
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
        PermanentNavigationDrawer(
            modifier = Modifier.padding(it),
            drawerContent = {
                PermanentDrawerSheet(
                    drawerShape = EndCornerShape(otherCorner = 0.dp)
                ) {
                    val selectedPage by component.selectedPage.subscribeAsState()

                    Spacer(modifier = Modifier.weight(1F))
                    component.pagerItems.forEachIndexed { index, pagerItem ->
                        NavigationDrawerItem(
                            selected = selectedPage == index,
                            icon = {
                                NavIcon(pagerItem)
                            },
                            onClick = {
                                component.selectPage(index)
                            },
                            label = {
                                Text(text = stringResource(pagerItem.label))
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                }
            }
        ) {
            ExpandedPages(
                pages = component.pages
            ) { _, page ->
                page.render()
            }
        }
    }
}