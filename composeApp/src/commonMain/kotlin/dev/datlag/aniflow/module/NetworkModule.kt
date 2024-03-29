package dev.datlag.aniflow.module

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.apollographql.apollo3.ApolloClient
import dev.datlag.aniflow.anilist.AiringTodayStateMachine
import dev.datlag.aniflow.anilist.PopularNextSeasonStateMachine
import dev.datlag.aniflow.anilist.PopularSeasonStateMachine
import dev.datlag.aniflow.anilist.TrendingAnimeStateMachine
import dev.datlag.aniflow.other.Constants
import dev.datlag.tooling.compose.ioDispatcher
import io.ktor.client.*
import okio.FileSystem
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import dev.datlag.aniflow.common.nullableFirebaseInstance
import org.kodein.di.bindProvider
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient

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
            ApolloClient.Builder()
                .dispatcher(ioDispatcher())
                .serverUrl(Constants.AniList.SERVER_URL)
                .build()
        }
        bindProvider<TrendingAnimeStateMachine> {
            TrendingAnimeStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindProvider<AiringTodayStateMachine> {
            AiringTodayStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindProvider<PopularSeasonStateMachine> {
            PopularSeasonStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindProvider<PopularNextSeasonStateMachine> {
            PopularNextSeasonStateMachine(
                client = instance(Constants.AniList.APOLLO_CLIENT),
                crashlytics = nullableFirebaseInstance()?.crashlytics
            )
        }
        bindSingleton<OpenIdConnectClient>(Constants.AniList.Auth.CLIENT) {
            OpenIdConnectClient {
                endpoints {
                    baseUrl(Constants.AniList.Auth.BASE_URL) {
                        authorizationEndpoint = "authorize"
                        tokenEndpoint = "token"
                    }
                }
                clientId = instance<String>(Constants.Sekret.ANILIST_CLIENT_ID).ifBlank { null }
                clientSecret = instance<String>(Constants.Sekret.ANILIST_CLIENT_SECRET).ifBlank { null }
                redirectUri = Constants.AniList.Auth.REDIRECT_URL
            }
        }
    }
}