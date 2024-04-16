package dev.datlag.aniflow.other

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import io.ktor.utils.io.*

actual object ImagePickerState {
    actual fun launch() {
    }
}

@Composable
actual fun rememberImagePickerState(onPick: (ByteArray?) -> Unit): ImagePickerState {
    return ImagePickerState
}