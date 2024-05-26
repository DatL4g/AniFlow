package dev.datlag.aniflow.other

data object Constants {

    const val GITHUB_REPO = "https://github.com/DatL4g/AniFlow"
    const val GITHUB_OWNER = "https://github.com/DatL4g"
    const val PRIVACY_POLICY = "$GITHUB_REPO/blob/master/Privacy_Policy.md"
    const val TERMS_CONDITIONS = "$GITHUB_REPO/blob/master/Terms_Conditions.md"

    const val SPDX_LICENSE_BASE = "https://spdx.org/licenses/"

    data object AniList {
        const val SERVER_URL = "https://graphql.anilist.co/"
        const val CACHE_FACTORY = "AniListCacheFactory"
        const val APOLLO_CLIENT = "AniListApolloClient"
        const val FALLBACK_APOLLO_CLIENT = "FallbackAniListApolloClient"

        data object Auth {
            const val BASE_URL = "https://anilist.co/api/v2/oauth/"
            const val REDIRECT_URL = "aniflow://anilist"
            const val CLIENT = "AniListAuthClient"
        }
    }

    data object Sponsor {
        const val GITHUB = "https://github.com/sponsors/DatL4g"
        const val POLAR = "https://polar.sh/DatL4g"
        const val PATREON = "https://patreon.com/datlag"
    }
}