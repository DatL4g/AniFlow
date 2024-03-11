package dev.datlag.aniflow.module

import coil3.ImageLoader
import coil3.PlatformContext
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.firebase.initialize
import dev.datlag.aniflow.firebaseIosApiKey
import dev.datlag.aniflow.firebaseIosApplication
import dev.datlag.aniflow.firebaseProject
import dev.datlag.aniflow.getPackageName
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

actual object PlatformModule {

    private const val NAME = "IOSPlatformModule"

    actual val di = DI.Module(NAME) {
        bindSingleton<Json> {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        bindSingleton<HttpClient> {
            HttpClient(Darwin) {
                engine {
                    configureRequest {
                        setAllowsCellularAccess(true)
                    }
                }
                install(ContentNegotiation) {
                    json(instance<Json>(), ContentType.Application.Json)
                    json(instance<Json>(), ContentType.Text.Plain)
                }
            }
        }
        bindSingleton<PlatformContext> {
            PlatformContext.INSTANCE
        }
        bindSingleton<FirebaseFactory> {
            FirebaseFactory.initialize(
                projectId = firebaseProject(getPackageName()),
                applicationId = firebaseIosApplication(getPackageName()),
                apiKey = firebaseIosApiKey(getPackageName()),
                googleAuthProvider = instanceOrNull()
            )
        }
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this
}