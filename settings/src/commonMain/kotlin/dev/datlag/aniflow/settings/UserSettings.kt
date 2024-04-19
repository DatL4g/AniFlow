package dev.datlag.aniflow.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserSettings(
    @ProtoNumber(1) val aniList: AniList = AniList(
        accessToken = null,
        refreshToken = null,
        idToken = null,
        expires = null
    )
) {
    @Serializable
    data class AniList(
        @ProtoNumber(1) val accessToken: String?,
        @ProtoNumber(2) val refreshToken: String?,
        @ProtoNumber(3) val idToken: String?,
        @ProtoNumber(4) val expires: Int?
    )
}
