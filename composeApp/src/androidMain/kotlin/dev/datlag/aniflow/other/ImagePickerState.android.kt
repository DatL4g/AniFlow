package dev.datlag.aniflow.other

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual data class ImagePickerState(
    private val mediaPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    actual fun launch() {
        mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

@Composable
actual fun rememberImagePickerState(onPick: (ByteArray?) -> Unit): ImagePickerState {
    val context = LocalContext.current
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onPick(
                uri?.let {
                    context.contentResolver.openInputStream(it)?.use { input ->
                        input.readBytes()
                    }
                }
            )
        }
    )

    return remember(mediaPicker) {
        ImagePickerState(
            mediaPicker
        )
    }
}