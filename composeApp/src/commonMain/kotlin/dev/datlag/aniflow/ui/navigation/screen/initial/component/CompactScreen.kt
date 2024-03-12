package dev.datlag.aniflow.ui.navigation.screen.initial.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.aniflow.LocalHaze
import dev.datlag.aniflow.LocalPaddingValues
import dev.datlag.aniflow.ui.navigation.screen.initial.InitialComponent
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CompactScreen(component: InitialComponent) {
    val selectedPage by component.selectedPage.subscribeAsState()

    Scaffold(
        bottomBar = {
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
        }
    ) {
        CompositionLocalProvider(
            LocalPaddingValues provides it
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
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