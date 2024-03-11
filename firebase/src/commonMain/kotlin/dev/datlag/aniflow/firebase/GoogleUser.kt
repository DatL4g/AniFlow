package dev.datlag.aniflow.firebase

import dev.datlag.sekret.Secret

/**
 * GoogleUser class holds most necessary fields
 */
data class GoogleUser(
    @Secret val idToken: String,
    @Secret val accessToken: String? = null
)
