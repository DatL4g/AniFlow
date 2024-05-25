package dev.datlag.aniflow.model

import kotlin.reflect.KClass

actual val KClass<*>.name: String
    get() = this.qualifiedName?.ifBlank { null }
        ?: this.simpleName?.ifBlank { null }
        ?: this.java.canonicalName?.ifBlank { null } ?: this.java.name.ifBlank { null } ?: this.toString()