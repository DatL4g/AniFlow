package dev.datlag.aniflow.other

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import io.ktor.utils.io.*

expect class ImagePickerState {
    fun launch()
}

@Composable
expect fun rememberImagePickerState(onPick: (ByteArray?) -> Unit = { }): ImagePickerState