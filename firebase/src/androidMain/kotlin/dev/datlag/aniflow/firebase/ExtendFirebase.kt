package dev.datlag.aniflow.firebase

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun FirebaseFactory.Companion.initialize(
    context: Context,
    projectId: String?,
    applicationId: String,
    apiKey: String,
    googleAuthProvider: GoogleAuthProvider?
): FirebaseFactory {
    return CommonFirebase(
        Firebase.initialize(
            context = context,
            options = FirebaseOptions(
                projectId = projectId,
                applicationId = applicationId,
                apiKey = apiKey
            )
        ),
        googleAuthProvider
    )
}