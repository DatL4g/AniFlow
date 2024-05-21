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
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.normalizedCache
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
import dev.datlag.aniflow.nekos.Nekos
import dev.datlag.aniflow.nekos.NekosRepository
import dev.datlag.aniflow.other.UserHelper
import dev.datlag.aniflow.settings.Settings
import dev.datlag.aniflow.trace.Trace
import dev.datlag.aniflow.trace.TraceRepository
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
                .normalizedCache(instance(Constants.AniList.CACHE_FACTORY))
                .fetchPolicy(FetchPolicy.CacheAndNetwork)
                .build()
        }
        bindSingleton<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT) {
            ApolloClient.Builder()
                .dispatcher(ioDispatcher())
                .serverUrl(Constants.AniList.SERVER_URL)
                .normalizedCache(instance(Constants.AniList.CACHE_FACTORY))
                .fetchPolicy(FetchPolicy.CacheAndNetwork)
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
        bindSingleton<Nekos> {
            val builder = instance<Ktorfit.Builder>()
            builder.build {
                baseUrl("https://api.nekosapi.com/v3/")
            }.create<Nekos>()
        }
        bindSingleton<CharacterRepository> {
            CharacterRepository(
                client = instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
                fallbackClient = instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
                isLoggedIn = instance<UserHelper>().isLoggedIn
            )
        }
        bindSingleton<MediumRepository> {
            MediumRepository(
                client = instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
                fallbackClient = instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
            )
        }
        bindSingleton<TraceRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            TraceRepository(
                trace = instance(),
                nsfw = appSettings.adultContent
            )
        }
        bindSingleton<NekosRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            NekosRepository(
                nekos = instance(),
                nsfw = appSettings.adultContent
            )
        }
        bindSingleton<ListRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            ListRepository(
                client = instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
                user = instance<UserHelper>().user,
                viewManga = appSettings.viewManga
            )
        }
        bindSingleton<SearchRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            SearchRepository(
                client = instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
                nsfw = appSettings.adultContent,
                viewManga = appSettings.viewManga
            )
        }
        bindSingleton<RecommendationRepository> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            RecommendationRepository(
                client = instance<ApolloClient>(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance<ApolloClient>(Constants.AniList.FALLBACK_APOLLO_CLIENT).newBuilder().fetchPolicy(FetchPolicy.NetworkFirst).build(),
                user = instance<UserHelper>().user,
                nsfw = appSettings.adultContent,
                viewManga = appSettings.viewManga
            )
        }

        bindProvider<TrendingStateMachine> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            TrendingStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance(Constants.AniList.FALLBACK_APOLLO_CLIENT),
                nsfw = appSettings.adultContent,
                viewManga = appSettings.viewManga,
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindProvider<PopularSeasonStateMachine> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            PopularSeasonStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance(Constants.AniList.FALLBACK_APOLLO_CLIENT),
                nsfw = appSettings.adultContent,
                viewManga = appSettings.viewManga,
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindProvider<PopularNextSeasonStateMachine> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            PopularNextSeasonStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance(Constants.AniList.FALLBACK_APOLLO_CLIENT),
                nsfw = appSettings.adultContent,
                viewManga = appSettings.viewManga,
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindProvider<AiringTodayStateMachine> {
            val appSettings = instance<Settings.PlatformAppSettings>()

            AiringTodayStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                fallbackClient = instance(Constants.AniList.FALLBACK_APOLLO_CLIENT),
                nsfw = appSettings.adultContent,
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
    }
}