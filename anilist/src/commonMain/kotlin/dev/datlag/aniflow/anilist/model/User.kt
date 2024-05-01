package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.ViewerMutation
import dev.datlag.aniflow.anilist.ViewerQuery
import dev.datlag.aniflow.anilist.type.UserStaffNameLanguage
import dev.datlag.aniflow.anilist.type.UserTitleLanguage
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val description: String? = null,
    val avatar: Avatar = Avatar(),
    val banner: String? = null,
    val displayAdultContent: Boolean = false,
    val profileColor: String? = null,
    val titleLanguage: TitleLanguage? = null,
    val charLanguage: CharLanguage? = null
) {
    constructor(query: ViewerQuery.Viewer) : this(
        id = query.id,
        name = query.name,
        description = query.about?.ifBlank { null },
        avatar = query.avatar.let(::Avatar),
        banner = query.bannerImage?.ifBlank { null },
        displayAdultContent = query.options?.displayAdultContent ?: false,
        profileColor = query.options?.profileColor?.ifBlank { null },
        titleLanguage = TitleLanguage.fromUser(query.options?.titleLanguage),
        charLanguage = CharLanguage.fromUser(query.options?.staffNameLanguage)
    )

    constructor(mutation: ViewerMutation.UpdateUser) : this(
        id = mutation.id,
        name = mutation.name,
        description = mutation.about?.ifBlank { null },
        avatar = mutation.avatar.let(::Avatar),
        banner = mutation.bannerImage?.ifBlank { null },
        displayAdultContent = mutation.options?.displayAdultContent ?: false,
        profileColor = mutation.options?.profileColor?.ifBlank { null },
        titleLanguage = TitleLanguage.fromUser(mutation.options?.titleLanguage),
        charLanguage = CharLanguage.fromUser(mutation.options?.staffNameLanguage)
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

    @Serializable
    sealed interface TitleLanguage {
        @Serializable
        data object Romaji : TitleLanguage

        @Serializable
        data object English : TitleLanguage

        @Serializable
        data object Native : TitleLanguage

        companion object {
            fun fromUser(user: UserTitleLanguage?): TitleLanguage? = when (user) {
                UserTitleLanguage.ROMAJI, UserTitleLanguage.ROMAJI_STYLISED -> Romaji
                UserTitleLanguage.ENGLISH, UserTitleLanguage.ENGLISH_STYLISED -> English
                UserTitleLanguage.NATIVE, UserTitleLanguage.NATIVE_STYLISED -> Native
                else -> null
            }
        }
    }

    @Serializable
    sealed interface CharLanguage {
        @Serializable
        data object RomajiWestern : CharLanguage

        @Serializable
        data object Romaji : CharLanguage

        @Serializable
        data object Native : CharLanguage

        companion object {
            fun fromUser(user: UserStaffNameLanguage?): CharLanguage? = when (user) {
                UserStaffNameLanguage.ROMAJI_WESTERN-> RomajiWestern
                UserStaffNameLanguage.ROMAJI -> Romaji
                UserStaffNameLanguage.NATIVE -> Native
                else -> null
            }
        }
    }
}
