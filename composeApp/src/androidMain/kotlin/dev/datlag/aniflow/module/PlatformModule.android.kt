package dev.datlag.aniflow.module

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import coil3.ImageLoader
import coil3.request.allowHardware
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import dev.datlag.aniflow.BuildKonfig
import dev.datlag.aniflow.Sekret
import dev.datlag.aniflow.firebase.FirebaseFactory
import dev.datlag.aniflow.firebase.initialize
import dev.datlag.aniflow.other.BurningSeriesResolver
import dev.datlag.aniflow.other.Constants
import dev.datlag.aniflow.other.StateSaver
import dev.datlag.aniflow.settings.*
import dev.datlag.tooling.createAsFileSafely
import dev.datlag.tooling.existsRWSafely
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toOkioPath
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
                install(HttpRequestRetry) {
                    retryOnExceptionOrServerErrors(3)
                    exponentialDelay()
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
                    projectId = Sekret.firebaseProject(BuildKonfig.packageName),
                    applicationId = Sekret.firebaseAndroidApplication(BuildKonfig.packageName)!!,
                    apiKey = Sekret.firebaseAndroidApiKey(BuildKonfig.packageName)!!,
                    googleAuthProvider = instanceOrNull(),
                    localLogger = object : FirebaseFactory.Crashlytics.LocalLogger {
                        override fun warn(message: String?) {
                            message?.let { Napier.w(it) }
                        }

                        override fun error(message: String?) {
                            message?.let { Napier.e(it) }
                        }

                        override fun error(throwable: Throwable?) {
                            throwable?.let { Napier.e("", it) }
                        }
                    }
                )
            } else {
                FirebaseFactory.Empty
            }
        }
        bindSingleton {
            val app: Context = instance()
            DataStoreFactory.create(
                storage = OkioStorage(
                    fileSystem = FileSystem.SYSTEM,
                    serializer = UserSettingsSerializer,
                    producePath = {
                        val path = app.filesDir.toOkioPath()
                            .resolve("datastore")
                            .resolve("user.settings").also {
                                it.toFile().createAsFileSafely()
                            }

                        path
                    }
                )
            )
        }
        bindSingleton {
            val app: Context = instance()
            DataStoreFactory.create(
                storage = OkioStorage(
                    fileSystem = FileSystem.SYSTEM,
                    serializer = AppSettingsSerializer,
                    producePath = {
                        val path = app.filesDir.toOkioPath()
                            .resolve("datastore")
                            .resolve("app.settings").also {
                                it.toFile().createAsFileSafely()
                            }

                        path
                    }
                )
            )
        }
        bindSingleton<Settings.PlatformUserSettings> {
            DataStoreUserSettings(instance())
        }
        bindSingleton<Settings.PlatformAppSettings> {
            DataStoreAppSettings(instance())
        }
        bindSingleton<BurningSeriesResolver> {
            BurningSeriesResolver(context = instance())
        }
        bindSingleton<NormalizedCacheFactory>(Constants.AniList.CACHE_FACTORY) {
            MemoryCacheFactory(maxSizeBytes = 25 * 1024 * 1024).chain(
                SqlNormalizedCacheFactory(context = instance(), name = "anilist.db")
            )
        }
    }
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this.allowHardware(false)
}