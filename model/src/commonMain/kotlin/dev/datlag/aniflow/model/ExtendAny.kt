package dev.datlag.aniflow.model

inline fun <T : Number> T.ifValue(value: T, defaultValue: () -> T): T {
    return if (this == value) defaultValue() else this
}

inline fun <T : Number> T?.ifValueOrNull(value: T, defaultValue: () -> T): T {
    return if (this == value || this == null) defaultValue() else this
}

fun Boolean.toInt() = if (this) 1 else 0