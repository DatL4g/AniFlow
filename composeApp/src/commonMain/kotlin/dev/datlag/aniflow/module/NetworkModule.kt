package dev.datlag.aniflow.module

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import dev.datlag.aniflow.BuildKonfig
import dev.datlag.aniflow.Sekret
import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.other.Constants
import dev.datlag.tooling.compose.ioDispatcher
import io.ktor.client.*
import okio.FileSystem
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import dev.datlag.aniflow.common.nullableFirebaseInstance
import dev.datlag.aniflow.model.safeFirstOrNull
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.trace.Trace
import dev.datlag.aniflow.trace.TraceStateMachine
import dev.datlag.tooling.async.suspendCatching
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import org.kodein.di.bindProvider

data object NetworkModule {

    const val NAME = "NetworkModule"

    val di = DI.Module(NAME) {
        import(PlatformModule.di)

        bindSingleton<ImageLoader> {
            ImageLoader.Builder(instance<PlatformContext>())
                .components {
                    add(KtorNetworkFetcherFactory(instance<HttpClient>()))
                    add(SvgDecoder.Factory())
                }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(instance<PlatformContext>())
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                        .maxSizeBytes(512L * 1024 * 1024) // 512 MB
                        .build()
                }
                .crossfade(true)
                .extendImageLoader()
                .build()
        }
        bindSingleton<ApolloClient>(Constants.AniList.APOLLO_CLIENT) {
            val userSettings = instance<Settings.PlatformUserSettings>()

            ApolloClient.Builder()
                .dispatcher(ioDispatcher())
                .serverUrl(Constants.AniList.SERVER_URL)
                .addHttpInterceptor(object : HttpInterceptor {
                    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
                        val req = request.newBuilder().apply {
                            val token = userSettings.aniList.map { it.accessToken }.safeFirstOrNull()

                            token?.let {
                                addHeader("Authorization", "Bearer $it")
                            }
                        }.build()
                        return chain.proceed(req)
                    }
                })
                .build()
        }
        bindSingleton<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT) {
            ApolloClient.Builder()
                .dispatcher(ioDispatcher())
                .serverUrl(Constants.AniList.SERVER_URL)
                .build()
        }
        bindSingleton<UserHelper> {
            UserHelper(
                userSettings = instance(),
                appSettings = instance(),
                client = instance(Constants.AniList.APOLLO_CLIENT),
                clientId = Sekret.anilistClientId(BuildKonfig.packageName)!!
            )
        }
        bindSingleton<Ktorfit.Builder> {
            ktorfitBuilder {
                httpClient(instance<HttpClient>())
            }
        }
        bindSingleton<Trace> {
            val builder = instance<Ktorfit.Builder>()
            builder.build {
                baseUrl("https://api.trace.moe/")
            }.create<Trace>()
        }
        bindProvider<TraceStateMachine> {
            TraceStateMachine(
                trace = instance(),
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindSingleton<TrendingRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            TrendingRepository(
                apolloClient = instance(Constants.AniList.APOLLO_CLIENT),
                nsfw = appSettings.adultContent
            )
        }
        bindSingleton<AiringTodayRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            AiringTodayRepository(
                apolloClient = instance(Constants.AniList.APOLLO_CLIENT),
                nsfw = appSettings.adultContent
            )
        }
        bindSingleton<PopularSeasonRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            PopularSeasonRepository(
                apolloClient = instance(Constants.AniList.APOLLO_CLIENT),
                nsfw = appSettings.adultContent
            )
        }
        bindSingleton<PopularNextSeasonRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            PopularNextSeasonRepository(
                apolloClient = instance(Constants.AniList.APOLLO_CLIENT),
                nsfw = appSettings.adultContent
            )
        }
    }
}