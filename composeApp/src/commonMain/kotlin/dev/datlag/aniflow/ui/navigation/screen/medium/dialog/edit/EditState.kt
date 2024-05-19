package dev.datlag.aniflow.ui.navigation.screen.medium.dialog.edit

import androidx.compose.runtime.*
import dev.datlag.aniflow.anilist.type.MediaListStatus
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlin.math.max
import kotlin.math.min

class EditState(
    private val initialStatus: MediaListStatus,
    private val episodeState: EpisodeState,
    private val repeatState: RepeatState,
) {

    val listStatus = MutableStateFlow(initialStatus)

    val episode = episodeState.currentEpisode
    val repeat = repeatState.currentCount

    val canRemoveEpisode: Boolean
        get() = episodeState.canRemove

    val canAddEpisode: Boolean
        get() = episodeState.canAdd

    val hasEpisodes: Boolean
        get() = episodeState.hasEpisodes

    val canRemoveRepeat: Boolean
        get() = repeatState.canRemove

    val canAddRepeat: Boolean
        get() = repeatState.canAdd

    init {
        if (listStatus.value == MediaListStatus.COMPLETED) {
            episodeState.complete()
        }
        if (episodeState.isCompleted) {
            if (listStatus.value != MediaListStatus.COMPLETED && listStatus.value != MediaListStatus.REPEATING) {
                listStatus.update {
                    MediaListStatus.COMPLETED
                }
            }
        }
    }

    fun plusEpisode(value: Int = 1) {
        episodeState.plus(
            value = value,
            onNotComplete = { notEmpty ->
                updateStatusNotCompleted(notEmpty)
            },
            onComplete = {
                updateStatusCompleted()
            }
        )
    }

    fun minusEpisode(value: Int = 1) {
        episodeState.minus(
            value = value,
            onNotComplete = { notEmpty ->
                updateStatusNotCompleted(notEmpty)
            }
        )
    }

    fun plusRepeat(value: Int = 1) {
        repeatState.plus(value) {
            listStatus.update {
                MediaListStatus.REPEATING
            }
        }
    }

    fun minusRepeat(value: Int = 1) {
        repeatState.minus(value)
    }

    fun setStatus(status: MediaListStatus) {
        val updated = listStatus.updateAndGet { status }

        if (updated == MediaListStatus.COMPLETED) {
            episodeState.complete()
        }
    }

    fun setEpisode(value: Int?) {
        episodeState.set(
            value = value,
            onNotComplete = { notEmpty ->
                updateStatusNotCompleted(notEmpty)
            },
            onComplete = {
                updateStatusCompleted()
            }
        )
    }

    fun setRepeat(value: Int?) {
        repeatState.set(value)
    }

    private fun updateStatusNotCompleted(notEmpty: Boolean) {
        listStatus.update {
            if (it == MediaListStatus.REPEATING) {
                if (notEmpty) {
                    it
                } else {
                    MediaListStatus.CURRENT
                }
            } else {
                if (it == MediaListStatus.COMPLETED || it == MediaListStatus.UNKNOWN__) {
                    MediaListStatus.CURRENT
                } else {
                    it
                }
            }
        }
    }

    private fun updateStatusCompleted() {
        listStatus.update {
            if (it != MediaListStatus.COMPLETED && it != MediaListStatus.REPEATING) {
                MediaListStatus.COMPLETED
            } else {
                it
            }
        }
    }

    class EpisodeState(
        private val maxEpisodes: Int,
        private val progress: Int?
    ) {
        internal val currentEpisode = MutableStateFlow(min(max(progress ?: 0, 0), maxEpisodes))

        internal val isCompleted: Boolean
            get() = currentEpisode.value == maxEpisodes

        internal val canRemove: Boolean
            get() = currentEpisode.value >= 1

        internal val canAdd: Boolean
            get() = currentEpisode.value < maxEpisodes

        internal val hasEpisodes: Boolean
            get() = maxEpisodes > 0

        internal fun plus(
            value: Int = 1,
            onNotComplete: (Boolean) -> Unit,
            onComplete: () -> Unit
        ) {
            val updated = currentEpisode.updateAndGet {
                min(max(it, 0) + value, maxEpisodes)
            }

            if (updated == maxEpisodes) {
                onComplete()
            } else {
                onNotComplete(updated > 0)
            }
        }

        internal fun minus(value: Int = 1, onNotComplete: (Boolean) -> Unit) {
            val updated = currentEpisode.updateAndGet {
                min(max(it - value, 0), maxEpisodes)
            }

            if (updated < maxEpisodes) {
                onNotComplete(updated > 0)
            }
        }

        internal fun complete() {
            currentEpisode.update {
                maxEpisodes
            }
        }

        internal fun set(
            value: Int?,
            onNotComplete: (Boolean) -> Unit,
            onComplete: () -> Unit
        ) {
            val updated = currentEpisode.updateAndGet {
                min(max(value ?: 0, 0), maxEpisodes)
            }

            if (updated == maxEpisodes) {
                onComplete()
            } else {
                onNotComplete(updated > 0)
            }
        }
    }

    class RepeatState(
        private val count: Int?
    ) {
        internal var currentCount = MutableStateFlow(max(count ?: 0, 0))

        internal val canRemove: Boolean
            get() = currentCount.value >= 1

        internal val canAdd: Boolean
            get() = true

        internal fun plus(value: Int = 1, onRepeating: () -> Unit) {
            val updated = currentCount.updateAndGet { it + 1 }

            if (updated >= 1) {
                onRepeating()
            }
        }

        internal fun minus(value: Int = 1) {
            currentCount.update {
                max(it - value, 0)
            }
        }

        internal fun set(value: Int?) {
            currentCount.update {
                max(value ?: 0, 0)
            }
        }
    }
}

@Composable
fun rememberEditState(
    mediumEpisodes: Flow<Int>,
    progress: Flow<Int?>,
    repeat: Flow<Int?>,
    listStatus: Flow<MediaListStatus>
): EditState {
    val maxEpisodes by mediumEpisodes.collectAsStateWithLifecycle(0)
    val prog by progress.collectAsStateWithLifecycle(null)
    val episodeState = remember(maxEpisodes, prog) {
        EditState.EpisodeState(
            maxEpisodes = maxEpisodes,
            progress = prog
        )
    }

    val repeatCount by repeat.collectAsStateWithLifecycle(null)
    val repeatState = remember(repeatCount) {
        EditState.RepeatState(repeatCount)
    }

    val status by listStatus.collectAsStateWithLifecycle(MediaListStatus.UNKNOWN__)
    return remember(status, episodeState, repeatState) {
        EditState(
            initialStatus = status,
            episodeState = episodeState,
            repeatState = repeatState,
        )
    }
}