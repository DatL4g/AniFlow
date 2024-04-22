package dev.datlag.aniflow.firebase

import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import java.util.logging.Logger

fun FirebaseFactory.Companion.initialize(
    projectId: String?,
    applicationId: String,
    apiKey: String,
    localLogger: FirebaseFactory.Crashlytics.LocalLogger?
) : FirebaseFactory {
    return CommonFirebase(
        Firebase.initialize(
            context = Application(),
            options = FirebaseOptions(
                projectId = projectId,
                applicationId = applicationId,
                apiKey = apiKey
            )
        ),
        null,
        localLogger ?: object : FirebaseFactory.Crashlytics.LocalLogger {
            override fun warn(message: String?) {
                Logger.getGlobal().warning(message)
            }

            override fun error(message: String?) {
                Logger.getGlobal().severe(message)
            }

            override fun error(throwable: Throwable?) {
                Logger.getGlobal().severe(throwable?.stackTraceToString() ?: throwable?.message)
            }
        }
    )
}