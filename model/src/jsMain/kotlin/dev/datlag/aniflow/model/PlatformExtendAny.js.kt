package dev.datlag.aniflow.model

import kotlin.reflect.KClass

actual val KClass<*>.name: String
    get() = this.simpleName?.ifBlank { null } ?: this.js.name.ifBlank { null } ?: this.toString()