package dev.datlag.aniflow.firebase

import android.content.Context
import android.util.Log
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun FirebaseFactory.Companion.initialize(
    context: Context,
    projectId: String?,
    applicationId: String,
    apiKey: String,
    googleAuthProvider: GoogleAuthProvider?,
    localLogger: FirebaseFactory.Crashlytics.LocalLogger?
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
        googleAuthProvider,
        localLogger = localLogger ?: object : FirebaseFactory.Crashlytics.LocalLogger {
            override fun warn(message: String?) {
                message?.let { Log.w(null, it) }
            }

            override fun error(message: String?) {
                message?.let { Log.e(null, it) }
            }

            override fun error(throwable: Throwable?) {
                throwable?.let { Log.e(null, null, throwable) }
            }
        }
    )
}