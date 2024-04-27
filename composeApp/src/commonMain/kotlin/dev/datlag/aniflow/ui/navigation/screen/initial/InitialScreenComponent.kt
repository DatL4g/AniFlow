package dev.datlag.aniflow.ui.navigation.screen.initial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import dev.datlag.aniflow.ui.navigation.screen.initial.home.HomeScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.initial.settings.SettingsScreenComponent
import org.kodein.di.DI

class InitialScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onMediumDetails: (Medium) -> Unit
) : InitialComponent, ComponentContext by componentContext {

    override val pagerItems: List<InitialComponent.PagerItem> = listOf(
        InitialComponent.PagerItem(
            label = SharedRes.strings.home,
            icon = Icons.Default.Home
        ),
        InitialComponent.PagerItem(
            label = SharedRes.strings.settings,
            icon = Icons.Default.Settings
        )
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private val pagesNavigation = PagesNavigation<View>()

    @OptIn(ExperimentalDecomposeApi::class)
    override val pages: Value<ChildPages<View, Component>> = childPages(
        source = pagesNavigation,
        serializer = View.serializer(),
        initialPages = {
            Pages(
                items = listOf(
                    View.Home,
                    View.Settings
                ),
                selectedIndex = 0
            )
        },
        childFactory = ::createChild
    )

    @OptIn(ExperimentalDecomposeApi::class)
    override val selectedPage: Value<Int> = pages.map { it.selectedIndex }

    @Composable
    override fun render() {
        onRender {
            InitialScreen(this)
        }
    }

    private fun createChild(
        view: View,
        componentContext: ComponentContext
    ): Component {
        return when (view) {
            is View.Home -> HomeScreenComponent(
                componentContext = componentContext,
                di = di,
                onMediumDetails = onMediumDetails
            )
            is View.Settings -> SettingsScreenComponent(
                componentContext = componentContext,
                di = di
            )
        }
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override fun selectPage(index: Int) {
        pagesNavigation.select(index = index) { new, old ->
            if (new.items[new.selectedIndex] == old.items[old.selectedIndex]) {
                (pages.value.items[pages.value.selectedIndex].instance as? ContentHolderComponent)?.dismissContent()
            }
        }
    }
}