package dev.datlag.aniflow.firebase

import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider as FirebaseGoogleProvider

open class CommonFirebase(
    private val app: FirebaseApp,
    private val googleAuthProvider: GoogleAuthProvider?,
    localLogger: FirebaseFactory.Crashlytics.LocalLogger?
) : FirebaseFactory {

    override val auth: FirebaseFactory.Auth = Auth(app, googleAuthProvider)
    override val crashlytics: FirebaseFactory.Crashlytics = Crashlytics(app, localLogger)

    data class Auth(
        private val app: FirebaseApp,
        private val googleAuthProvider: GoogleAuthProvider?
    ) : FirebaseFactory.Auth {

        override val isSignedIn: Boolean
            get() = Firebase.auth(app).currentUser != null

        override val googleAuthSupported: Boolean
            get() = googleAuthProvider != null && googleAuthProvider !is GoogleAuthProvider.Empty

        override suspend fun loginOrCreateEmail(email: String, password: String): Boolean {
            val auth = Firebase.auth(app)

            val loginResult = suspendCatching {
                auth.signInWithEmailAndPassword(email, password)
            }.getOrNull()

            return if (loginResult?.user == null) {
                val createResult = suspendCatching {
                    auth.createUserWithEmailAndPassword(email, password)
                }.getOrNull()

                createResult?.user != null
            } else {
                true
            }
        }

        override suspend fun signOut() {
            Firebase.auth(app).signOut()
            googleAuthProvider?.signOut()
        }

        override suspend fun resetPassword(email: String) {
            Firebase.auth(app).sendPasswordResetEmail(email)
        }

        override suspend fun googleSignIn(googleUser: GoogleUser?): Boolean {
            return if (googleUser != null) {
                val authCredential = FirebaseGoogleProvider.credential(googleUser.idToken, googleUser.accessToken)
                val result = suspendCatching {
                    Firebase.auth(app).signInWithCredential(authCredential)
                }.getOrNull()

                result?.user != null
            } else {
                false
            }
        }
    }

    data class Crashlytics(
        private val app: FirebaseApp,
        override val localLogger: FirebaseFactory.Crashlytics.LocalLogger?
    ) : FirebaseFactory.Crashlytics {
        override fun customKey(key: String, value: String) {
            crashlyticsCustomKey(app, key, value)
        }
        override fun customKey(key: String, value: Boolean) {
            crashlyticsCustomKey(app, key, value)
        }
        override fun customKey(key: String, value: Int) {
            crashlyticsCustomKey(app, key, value)
        }
        override fun customKey(key: String, value: Long) {
            crashlyticsCustomKey(app, key, value)
        }
        override fun customKey(key: String, value: Float) {
            crashlyticsCustomKey(app, key, value)
        }
        override fun customKey(key: String, value: Double) {
            crashlyticsCustomKey(app, key, value)
        }
        override fun log(throwable: Throwable?) {
            localLogger?.error(throwable)
            crashlyticsLog(app, throwable)
        }
        override fun log(message: String?) {
            localLogger?.warn(message)
            crashlyticsLog(app, message)
        }
    }
}