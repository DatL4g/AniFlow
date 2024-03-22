package dev.datlag.aniflow.common

import dev.datlag.aniflow.anilist.model.Medium

fun Medium.preferred(): String {
    return this.title.preferred().ifBlank { this.id.toString() }
}

expect fun Medium.Title.preferred(): String