package dev.datlag.aniflow.ui.navigation

interface DialogComponent : ContentHolderComponent {
    fun dismiss()

    override fun dismissContent() {
        dismiss()
    }
}