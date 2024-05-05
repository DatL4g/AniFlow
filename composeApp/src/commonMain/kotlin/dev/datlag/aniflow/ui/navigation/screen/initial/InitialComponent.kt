package dev.datlag.aniflow.ui.navigation.screen.initial

import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.ui.navigation.Component
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.Flow

interface InitialComponent : Component {
    val viewing: Flow<MediaType>
    val pagerItems: List<PagerItem>
    val selectedPage: Value<Int>

    @OptIn(ExperimentalDecomposeApi::class)
    val pages: Value<ChildPages<View, Component>>

    fun selectPage(index: Int)
    fun viewProfile()
    fun viewAnime()
    fun viewManga()

    data class PagerItem(
        val label: StringResource,
        val icon: ImageVector
    )
}