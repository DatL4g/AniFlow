package dev.datlag.aniflow.other

data object Constants {

    data object AniList {
        const val SERVER_URL = "https://graphql.anilist.co/"
        const val APOLLO_CLIENT = "AniListApolloClient"
        const val FALLBACK_APOLLO_CLIENT = "FallbackAniListApolloClient"

        data object Auth {
            const val BASE_URL = "https://anilist.co/api/v2/oauth/"
            const val REDIRECT_URL = "aniflow://anilist"
            const val CLIENT = "AniListAuthClient"
        }
    }
}