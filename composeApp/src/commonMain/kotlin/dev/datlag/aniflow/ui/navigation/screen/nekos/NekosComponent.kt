package dev.datlag.aniflow.ui.navigation.screen.nekos

import dev.datlag.aniflow.nekos.NekosRepository
import dev.datlag.aniflow.nekos.model.Rating
import dev.datlag.aniflow.ui.navigation.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NekosComponent : Component {

    val adultContent: Flow<Boolean>
    val rating: StateFlow<Rating>

    val state: Flow<NekosRepository.State>

    fun back()
    fun filter(rating: Rating)
}