package dev.datlag.aniflow.ui.navigation.screen.initial.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.datlag.aniflow.common.isScrollingUp
import dev.datlag.aniflow.ui.custom.ExpandedPages
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialComponent
import dev.datlag.aniflow.ui.navigation.screen.initial.model.FABConfig
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MediumScreen(component: InitialComponent) {
    Scaffold(
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
        Row(
            modifier = Modifier.padding(it)
        ) {
            NavigationRail {
                val selectedPage by component.selectedPage.subscribeAsState()

                Spacer(modifier = Modifier.weight(1F))
                component.pagerItems.forEachIndexed { index, pagerItem ->
                    NavigationRailItem(
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
                Spacer(modifier = Modifier.weight(1F))
            }

            ExpandedPages(
                pages = component.pages
            ) { _, page ->
                page.render()
            }
        }
    }
}