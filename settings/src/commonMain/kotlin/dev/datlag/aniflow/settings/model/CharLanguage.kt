package dev.datlag.aniflow.settings.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CharLanguage.CharSerializer::class)
sealed interface CharLanguage {
    val id: Int

    @Serializable
    data object RomajiWestern : CharLanguage {
        override val id: Int = 1
    }

    @Serializable
    data object Romaji : CharLanguage {
        override val id: Int = 2
    }

    @Serializable
    data object Native : CharLanguage {
        override val id: Int = 3
    }

    companion object CharSerializer : KSerializer<CharLanguage?> {
        val all: Set<CharLanguage> = setOf(
            RomajiWestern,
            Romaji,
            Native
        )

        internal fun fromId(value: Int): CharLanguage? = when (value) {
            1 -> RomajiWestern
            2 -> Romaji
            3 -> Native
            else -> null
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CharLanguage", PrimitiveKind.INT)

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): CharLanguage? {
            return if (decoder.decodeNotNullMark()) {
                fromId(decoder.decodeInt())
            } else {
                decoder.decodeNull()
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: CharLanguage?) {
            if (value != null) {
                encoder.encodeNotNullMark()
                encoder.encodeInt(value.id)
            } else {
                encoder.encodeNull()
            }
        }
    }
}