package dev.datlag.aniflow.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

actual fun String.htmlToAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@htmlToAnnotatedString)
    }
}