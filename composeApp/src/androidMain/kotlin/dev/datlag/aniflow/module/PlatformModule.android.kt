package dev.datlag.aniflow.module

import android.content.Context
import androidx.credentials.CredentialManager
import coil3.ImageLoader
import coil3.request.allowHardware
import dev.datlag.aniflow.Sekret
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.firebase.initialize
import dev.datlag.aniflow.getPackageName
import dev.datlag.aniflow.other.StateSaver
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

actual object PlatformModule {

    private const val NAME = "AndroidPlatformModule"

    actual val di = DI.Module(NAME) {
        bindSingleton<Json> {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        bindSingleton<HttpClient> {
            HttpClient(OkHttp) {
                engine {
                    config {
                        followRedirects(true)
                        followSslRedirects(true)
                    }
                }
                install(ContentNegotiation) {
                    json(instance<Json>(), ContentType.Application.Json)
                    json(instance<Json>(), ContentType.Text.Plain)
                }
            }
        }
        bindSingleton<CredentialManager> {
            CredentialManager.create(instance())
        }
        bindSingleton<FirebaseFactory> {
            if (StateSaver.sekretLibraryLoaded) {
                FirebaseFactory.initialize(
                    context = instance<Context>(),
                    projectId = Sekret.firebaseProject(getPackageName()),
                    applicationId = Sekret.firebaseAndroidApplication(getPackageName())!!,
                    apiKey = Sekret.firebaseAndroidApiKey(getPackageName())!!,
                    googleAuthProvider = instanceOrNull()
                )
            } else {
                FirebaseFactory.Empty
            }
        }
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this.allowHardware(false)
}