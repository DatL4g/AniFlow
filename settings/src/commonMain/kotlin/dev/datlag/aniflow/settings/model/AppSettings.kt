package dev.datlag.aniflow.settings.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class AppSettings(
    @ProtoNumber(1) val adultContent: Boolean = false
)
