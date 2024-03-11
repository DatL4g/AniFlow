import androidx.compose.ui.window.ComposeUIViewController
import dev.datlag.aniflow.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
