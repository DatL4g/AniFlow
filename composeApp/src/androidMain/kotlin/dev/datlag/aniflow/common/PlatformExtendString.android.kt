package dev.datlag.aniflow.common

import androidx.compose.ui.text.AnnotatedString
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString as htmlAsAnnotated

actual fun String.htmlToAnnotatedString(): AnnotatedString {
    return htmlAsAnnotated(
        html = this
    )
}