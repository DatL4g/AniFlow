package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit

import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.ui.navigation.DialogComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface EditComponent : DialogComponent {
    val episodes: Flow<Int>
    val progress: Flow<Int?>
    val listStatus: Flow<MediaListStatus>
    val repeatCount: Flow<Int?>
    val episodeStartDate: Flow<LocalDate?>
}