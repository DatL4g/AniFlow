package dev.datlag.aniflow.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class UserSettings(
    @ProtoNumber(1) val aniList: AniList = AniList()
) {
    @Serializable
    data class AniList(
        @ProtoNumber(1) val accessToken: String? = null,
        @ProtoNumber(2) val refreshToken: String? = null,
        @ProtoNumber(3) val idToken: String? = null,
    )
}
