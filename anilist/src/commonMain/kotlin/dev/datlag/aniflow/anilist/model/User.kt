package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.ViewerMutation
import dev.datlag.aniflow.anilist.ViewerQuery
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val description: String? = null,
    val avatar: Avatar = Avatar(),
    val banner: String? = null,
    val displayAdultContent: Boolean = false,
    val profileColor: String? = null
) {
    constructor(query: ViewerQuery.Viewer) : this(
        id = query.id,
        name = query.name,
        description = query.about?.ifBlank { null },
        avatar = query.avatar.let(::Avatar),
        banner = query.bannerImage?.ifBlank { null },
        displayAdultContent = query.options?.displayAdultContent ?: false,
        profileColor = query.options?.profileColor?.ifBlank { null }
    )

    constructor(mutation: ViewerMutation.UpdateUser) : this(
        id = mutation.id,
        name = mutation.name,
        description = mutation.about?.ifBlank { null },
        avatar = mutation.avatar.let(::Avatar),
        banner = mutation.bannerImage?.ifBlank { null },
        displayAdultContent = mutation.options?.displayAdultContent ?: false,
        profileColor = mutation.options?.profileColor?.ifBlank { null }
    )

    @Serializable
    data class Avatar(
        val medium: String? = null,
        val large: String? = null
    ) {
        constructor(query: ViewerQuery.Avatar?) : this(
            medium = query?.medium?.ifBlank { null },
            large = query?.large?.ifBlank { null }
        )

        constructor(mutation: ViewerMutation.Avatar?) : this(
            medium = mutation?.medium?.ifBlank { null },
            large = mutation?.large?.ifBlank { null }
        )
    }
}
