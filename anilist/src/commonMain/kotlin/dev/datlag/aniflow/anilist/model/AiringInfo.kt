package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.AiringQuery
import kotlinx.serialization.Serializable

@Serializable
data class AiringInfo(
    val airingAt: Int,
    val episode: Int,
    val medium: Medium
) {
    companion object {
        operator fun invoke(schedule: AiringQuery.AiringSchedule): AiringInfo? {
            val medium = schedule.media?.let(::Medium) ?: return null

            return AiringInfo(
                airingAt = schedule.airingAt,
                episode = schedule.episode,
                medium = medium
            )
        }
    }
}