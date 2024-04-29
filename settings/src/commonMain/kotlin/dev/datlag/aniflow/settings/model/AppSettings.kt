package dev.datlag.aniflow.settings.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class AppSettings(
    @ProtoNumber(1) val adultContent: Boolean = false,
    @ProtoNumber(2) val color: Color?,
    @ProtoNumber(3) val titleLanguage: TitleLanguage?,
) {

    @Serializable(with = Color.ColorSerializer::class)
    sealed interface Color {
        val hex: String
        val label: String
            get() = hex

        @Serializable
        data object Blue : Color {
            override val hex: String = "#3db4f2"
            override val label: String = "blue"
        }

        @Serializable
        data object Purple : Color {
            override val hex: String = "#c063ff"
            override val label: String = "purple"
        }

        @Serializable
        data object Pink : Color {
            override val hex: String = "#fc9dd6"
            override val label: String = "pink"
        }

        @Serializable
        data object Orange : Color {
            override val hex: String = "#ef881a"
            override val label: String = "orange"
        }

        @Serializable
        data object Red : Color {
            override val hex: String = "#e13333"
            override val label: String = "red"
        }

        @Serializable
        data object Green : Color {
            override val hex: String = "#4cca51"
            override val label: String = "green"
        }

        @Serializable
        data object Gray : Color {
            override val hex: String = "#677b94"
            override val label: String = "gray"
        }

        @Serializable
        data class Custom(
            override val hex: String
        ) : Color

        companion object ColorSerializer : KSerializer<Color?> {
            val all: Set<Color> = setOf(
                Blue,
                Purple,
                Pink,
                Orange,
                Red,
                Green,
                Gray
            )

            fun fromString(value: String?): Color? = when {
                value == null -> null
                value.equals(Blue.label, ignoreCase = true) -> Blue
                value.equals(Purple.label, ignoreCase = true) -> Purple
                value.equals(Pink.label, ignoreCase = true) -> Pink
                value.equals(Orange.label, ignoreCase = true) -> Orange
                value.equals(Red.label, ignoreCase = true) -> Red
                value.equals(Green.label, ignoreCase = true) -> Green
                value.equals(Gray.label, ignoreCase = true) || value.equals("grey", ignoreCase = true) -> Gray
                else -> {
                    if (value.startsWith("#")) {
                        Custom(value)
                    } else {
                        null
                    }
                }
            }

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): Color? {
                return if (decoder.decodeNotNullMark()) {
                    fromString(decoder.decodeString())
                } else {
                    decoder.decodeNull()
                }
            }

            override fun serialize(encoder: Encoder, value: Color?) {
                if (value != null) {
                    encoder.encodeNotNullMark()
                    encoder.encodeString(value.label)
                } else {
                    encoder.encodeNull()
                }
            }
        }
    }

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
            val all: Set<TitleLanguage> = setOf(
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

            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Title", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): TitleLanguage? {
                return if (decoder.decodeNotNullMark()) {
                    fromId(decoder.decodeInt())
                } else {
                    decoder.decodeNull()
                }
            }

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
}
