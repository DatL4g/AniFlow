package dev.datlag.aniflow.firebase

interface GoogleAuthProvider {

    val credentials: GoogleAuthCredentials

    suspend fun signOut()

    data object Empty : GoogleAuthProvider {

        override val credentials: GoogleAuthCredentials = GoogleAuthCredentials("")

        override suspend fun signOut() { }
    }

    companion object
}