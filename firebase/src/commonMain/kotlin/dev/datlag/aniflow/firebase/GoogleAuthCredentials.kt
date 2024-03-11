package dev.datlag.aniflow.firebase

import dev.datlag.sekret.Secret

/**
 * Google Auth Credentials holder class.
 * @param serverId - This should be Web Client Id that you created in Google OAuth page
 */
data class GoogleAuthCredentials(
    @Secret val serverId: String
)