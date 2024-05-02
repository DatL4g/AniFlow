package dev.datlag.aniflow.model

inline fun <T : Number> T.ifValue(value: T, defaultValue: () -> T): T {
    return if (this == value) defaultValue() else this
}

inline fun <T : Number> T.asNullIf(value: T): T? {
    return if (this == value) null else this
}

inline fun <T : Number> T?.ifValueOrNull(value: T, defaultValue: () -> T): T {
    return if (this == value || this == null) defaultValue() else this
}

fun Boolean.toInt() = if (this) 1 else 0

fun StringBuilder.appendWithSpace(str: String?): StringBuilder {
    var builder = this
    if (!str.isNullOrBlank()) {
        builder = builder.append(str)
        builder = builder.append(" ")
    }
    return builder
}