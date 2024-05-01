package dev.datlag.aniflow.settings.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class AppSettings(
    @ProtoNumber(1) val adultContent: Boolean = false,
    @ProtoNumber(2) val color: Color?,
    @ProtoNumber(3) val titleLanguage: TitleLanguage?,
    @ProtoNumber(4) val charLanguage: CharLanguage?
)
