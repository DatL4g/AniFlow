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
import dev.datlag.aniflow.other.TokenRefreshHandler
import dev.datlag.tooling.async.suspendCatching
import io.github.aakira.napier.Napier
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
            val oidc = instance<OpenIdConnectClient>(Constants.AniList.Auth.CLIENT)
            val tokenHandler = instance<TokenRefreshHandler>()

            ApolloClient.Builder()
                .dispatcher(ioDispatcher())
                .serverUrl(Constants.AniList.SERVER_URL)
                .addHttpInterceptor(object : HttpInterceptor {
                    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
                        val req = request.newBuilder().apply {
                            val refreshedToken = tokenHandler.getAccessToken()?.let {
                                suspendCatching {
                                    tokenHandler.refreshAndSaveToken(oidc, it).accessToken
                                }.getOrNull() ?: it
                            }

                            refreshedToken?.let {
                                addHeader("Authorization", "Bearer $it")
                            }
                        }.build()
                        return chain.proceed(req)
                    }
                })
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
        bindSingleton<TokenRefreshHandler> {
            TokenRefreshHandler(instance())
        }
    }
}