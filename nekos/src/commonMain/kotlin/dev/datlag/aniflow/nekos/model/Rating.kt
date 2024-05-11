package dev.datlag.aniflow.nekos.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface Rating {
    val query: String

    @Serializable
    data object Safe : Rating {
        override val query: String = "safe"
    }

    @Serializable
    data object Suggestive : Rating {
        override val query: String = "suggestive"
    }

    @Serializable
    data object Borderline : Rating {
        override val query: String = "borderline"
    }

    @Serializable
    data object Explicit : Rating {
        override val query: String = "explicit"
    }
}