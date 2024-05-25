package dev.datlag.aniflow.settings.model

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TitleLanguage.TitleSerializer::class)
sealed interface TitleLanguage {
    val id: Int

    @Serializable
    data object Romaji : TitleLanguage {
        override val id: Int = 1
    }

    @Serializable
    data object English : TitleLanguage {
        override val id: Int = 2
    }

    @Serializable
    data object Native : TitleLanguage {
        override val id: Int = 3
    }

    companion object TitleSerializer : KSerializer<TitleLanguage?> {
        val all: ImmutableSet<TitleLanguage> = persistentSetOf(
            Romaji,
            English,
            Native
        )

        internal fun fromId(value: Int): TitleLanguage? = when (value) {
            1 -> Romaji
            2 -> English
            3 -> Native
            else -> null
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TitleLanguage", PrimitiveKind.INT)

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): TitleLanguage? {
            return if (decoder.decodeNotNullMark()) {
                fromId(decoder.decodeInt())
            } else {
                decoder.decodeNull()
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: TitleLanguage?) {
            if (value != null) {
                encoder.encodeNotNullMark()
                encoder.encodeInt(value.id)
            } else {
                encoder.encodeNull()
            }
        }
    }
}