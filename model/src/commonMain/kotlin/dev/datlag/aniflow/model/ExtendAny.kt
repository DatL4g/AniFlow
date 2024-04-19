package dev.datlag.aniflow.model

inline fun Boolean.alsoTrue(block: Boolean.() -> Unit): Boolean {
    if (this) {
        block(this)
    }
    return this
}

inline fun Boolean.alsoFalse(block: Boolean.() -> Unit): Boolean {
    if (!this) {
        block(this)
    }
    return this
}