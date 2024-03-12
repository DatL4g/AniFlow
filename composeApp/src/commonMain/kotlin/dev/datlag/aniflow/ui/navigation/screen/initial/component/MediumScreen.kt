package dev.datlag.aniflow.ui.navigation.screen.initial.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.datlag.aniflow.ui.custom.ExpandedPages
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialComponent
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MediumScreen(component: InitialComponent) {
    Scaffold {
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