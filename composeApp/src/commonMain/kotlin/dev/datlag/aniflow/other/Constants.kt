package dev.datlag.aniflow.other

data object Constants {

    data object AniList {
        const val SERVER_URL = "https://graphql.anilist.co/"
        const val APOLLO_CLIENT = "AniListApolloClient"

        data object Auth {
            const val BASE_URL = "https://anilist.co/api/v2/oauth/"
            const val REDIRECT_URL = "datlag://aniflow/anilist"
            const val CLIENT = "AniListAuthClient"
        }
    }

    data object Sekret {
        const val ANILIST_CLIENT_ID = "AniListClientId"
        const val ANILIST_CLIENT_SECRET = "AniListClientSecret"
    }
}