package dev.datlag.aniflow.ui.navigation.screen.initial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
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
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.onRender
import dev.datlag.aniflow.model.coroutines.Executor
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.ui.navigation.Component
import dev.datlag.aniflow.ui.navigation.ContentHolderComponent
import dev.datlag.aniflow.ui.navigation.screen.initial.favorites.FavoritesScreenComponent
import dev.datlag.aniflow.ui.navigation.screen.settings.SettingsScreenComponent
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance

class InitialScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onMediumDetails: (Medium) -> Unit,
    private val onProfile: () -> Unit
) : InitialComponent, ComponentContext by componentContext {

    private val appSettings by di.instance<Settings.PlatformAppSettings>()

    override val pagerItems: List<InitialComponent.PagerItem> = listOf(
        InitialComponent.PagerItem(
            label = SharedRes.strings.home,
            icon = Icons.Default.Home
        ),
        InitialComponent.PagerItem(
            label = SharedRes.strings.favorites,
            icon = Icons.Filled.Favorite
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
                    View.Favorites
                ),
                selectedIndex = 0
            )
        },
        childFactory = ::createChild
    )

    @OptIn(ExperimentalDecomposeApi::class)
    override val selectedPage: Value<Int> = pages.map { it.selectedIndex }

    private val viewTypeExecutor = Executor()

    override val viewing = appSettings.viewManga.map {
        if (it) {
            MediaType.MANGA
        } else {
            MediaType.ANIME
        }
    }

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
        return FavoritesScreenComponent(
            componentContext = componentContext,
            di = di
        )
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override fun selectPage(index: Int) {
        pagesNavigation.select(index = index) { new, old ->
            if (new.items[new.selectedIndex] == old.items[old.selectedIndex]) {
                (pages.value.items[pages.value.selectedIndex].instance as? ContentHolderComponent)?.dismissContent()
            }
        }
    }

    override fun viewProfile() {
        onProfile()
    }

    override fun viewAnime() {
        StateSaver.Home.updateAllLoading()
        launchIO {
            viewTypeExecutor.enqueue {
                appSettings.setViewManga(false)
            }
        }
    }

    override fun viewManga() {
        StateSaver.Home.updateAllLoading()
        launchIO {
            viewTypeExecutor.enqueue {
                appSettings.setViewManga(true)
            }
        }
    }
}