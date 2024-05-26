package dev.datlag.aniflow.other

actual class ConsentInfo {
    actual val privacy: Boolean
        get() = false

    actual fun initialize() { }

    actual fun reset() { }

    actual fun showPrivacyForm() { }

}