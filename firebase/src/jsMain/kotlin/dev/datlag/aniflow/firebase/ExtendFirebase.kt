package dev.datlag.aniflow.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun FirebaseFactory.Companion.initialize(
    projectId: String?,
    applicationId: String,
    apiKey: String,
    googleAuthProvider: GoogleAuthProvider?,
    localLogger: FirebaseFactory.Crashlytics.LocalLogger?
) : FirebaseFactory {
    return CommonFirebase(
        Firebase.initialize(
            context = null,
            options = FirebaseOptions(
                projectId = projectId,
                applicationId = applicationId,
                apiKey = apiKey
            )
        ),
        googleAuthProvider,
        localLogger ?: object : FirebaseFactory.Crashlytics.LocalLogger {
            override fun warn(message: String?) {
                console.warn(message)
            }

            override fun error(throwable: Throwable?) {
                console.error(throwable)
            }

            override fun error(message: String?) {
                console.error(message)
            }
        }
    )
}