package dev.datlag.aniflow.ui.navigation.screen.wallpaper

import dev.datlag.aniflow.nekos.NekosRepository
import dev.datlag.aniflow.nekos.model.Rating
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WallpaperComponent : Component {

    val adultContent: Flow<Boolean>
    val rating: StateFlow<Rating>

    val state: Flow<NekosRepository.State>

    fun viewHome()
    fun viewFavorites()

    fun filter(rating: Rating)
}