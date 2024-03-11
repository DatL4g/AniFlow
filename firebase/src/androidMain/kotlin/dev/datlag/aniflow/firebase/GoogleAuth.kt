package dev.datlag.aniflow.firebase

import androidx.credentials.*

class GoogleAuth(
    override val credentials: GoogleAuthCredentials,
    private val credentialManager: CredentialManager
) : GoogleAuthProvider {

    constructor(serverId: String, credentialManager: CredentialManager) : this(
        GoogleAuthCredentials(serverId),
        credentialManager
    )

    override suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}