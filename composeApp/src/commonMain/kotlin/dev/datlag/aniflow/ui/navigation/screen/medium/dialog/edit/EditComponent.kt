package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit

import dev.datlag.aniflow.other.Series
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.Flow

interface EditComponent : DialogComponent {

    val bsAvailable: Boolean
    val bsOptions: Flow<Collection<Series>>
}