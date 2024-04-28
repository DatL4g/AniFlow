package dev.datlag.aniflow.settings.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserSettings(
    @ProtoNumber(1) val aniList: AniList = AniList(
        accessToken = null,
        expires = null
    )
) {
    @Serializable
    data class AniList(
        @ProtoNumber(1) val accessToken: String?,
        @ProtoNumber(2) val expires: Int?
    )
}
