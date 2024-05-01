package dev.datlag.aniflow.settings.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Color? {
            return if (decoder.decodeNotNullMark()) {
                fromString(decoder.decodeString())
            } else {
                decoder.decodeNull()
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
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