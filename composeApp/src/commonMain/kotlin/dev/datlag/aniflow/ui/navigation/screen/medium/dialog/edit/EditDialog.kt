package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.aniflow.LocalEdgeToEdge
import dev.datlag.aniflow.SharedRes
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.aniflow.common.icon
import dev.datlag.aniflow.common.merge
import dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit.component.TopSection
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditDialog(component: EditComponent) {
    val sheetState = rememberModalBottomSheetState()
    val (insets, bottomPadding) = if (LocalEdgeToEdge.current) {
        WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ) to BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()
    } else {
        BottomSheetDefaults.windowInsets to PaddingValues()
    }

    ModalBottomSheet(
        onDismissRequest = component::dismiss,
        windowInsets = insets,
        sheetState = sheetState
    ) {
        val editState = rememberEditState(
            mediumEpisodes = component.episodesOrChapters,
            progress = component.progress,
            repeat = component.repeatCount,
            listStatus = component.listStatus,
        )
        val currentListStatus by editState.listStatus.collectAsStateWithLifecycle()
        val type by component.type.collectAsStateWithLifecycle(MediaType.UNKNOWN__)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = bottomPadding.merge(
                PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 48.dp
                )
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                TopSection(
                    state = sheetState,
                    modifier = Modifier.fillParentMaxWidth(),
                    onBack = component::dismiss,
                    onSave = {
                        component.save(editState)
                    }
                )
            }
            item {
                Text(
                    modifier = Modifier.fillParentMaxWidth().padding(top = 32.dp),
                    text = stringResource(
                        if (type == MediaType.MANGA) {
                            SharedRes.strings.read_chapter
                        } else {
                            SharedRes.strings.watched_episode
                        }
                    ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
            item {
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    MediaListStatus.knownEntries.forEach { entry ->
                        val selected = remember(entry, currentListStatus) { entry == currentListStatus }

                        IconButton(
                            onClick = {
                                editState.setStatus(entry)
                            },
                            enabled = !selected
                        ) {
                            Icon(
                                imageVector = entry.icon(),
                                contentDescription = null,
                                tint = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    LocalContentColor.current
                                }
                            )
                        }
                    }
                }
            }
            if (editState.hasEpisodes) {
                item {
                    val currentEpisode by editState.episode.collectAsStateWithLifecycle()

                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                editState.minusEpisode()
                            },
                            enabled = editState.canRemoveEpisode
                        ) {
                            Text(text = stringResource(SharedRes.strings.minus_one))
                        }
                        OutlinedTextField(
                            modifier = Modifier.weight(1F),
                            value = if (currentEpisode <= 0) "" else currentEpisode.toString(),
                            onValueChange = {
                                editState.setEpisode(it.toIntOrNull())
                            },
                            placeholder = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(
                                        if (type == MediaType.MANGA) {
                                            SharedRes.strings.chapter
                                        } else {
                                            SharedRes.strings.episode
                                        }
                                    ),
                                    textAlign = TextAlign.Center,
                                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                )
                            },
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            shape = MaterialTheme.shapes.medium
                        )
                        Button(
                            onClick = {
                                editState.plusEpisode()
                            },
                            enabled = editState.canAddEpisode
                        ) {
                            Text(text = stringResource(SharedRes.strings.plus_one))
                        }
                    }
                }
            }
            if (currentListStatus == MediaListStatus.REPEATING) {
                item {
                    val currentRepeating by editState.repeat.collectAsStateWithLifecycle()

                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                editState.minusRepeat()
                            },
                            enabled = editState.canRemoveRepeat
                        ) {
                            Text(text = stringResource(SharedRes.strings.minus_one))
                        }
                        OutlinedTextField(
                            modifier = Modifier.weight(1F),
                            value = if (currentRepeating <= 0) "" else currentRepeating.toString(),
                            onValueChange = {
                                editState.setRepeat(it.toIntOrNull())
                            },
                            placeholder = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(SharedRes.strings.repeat),
                                    textAlign = TextAlign.Center,
                                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                )
                            },
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            shape = MaterialTheme.shapes.medium
                        )
                        Button(
                            onClick = {
                                editState.plusRepeat()
                            },
                            enabled = editState.canAddRepeat
                        ) {
                            Text(text = stringResource(SharedRes.strings.plus_one))
                        }
                    }
                }
            }
        }
    }
}