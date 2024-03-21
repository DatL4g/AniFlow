package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.AiringQuery
import dev.datlag.aniflow.anilist.SeasonQuery
import dev.datlag.aniflow.anilist.TrendingQuery
import kotlinx.serialization.Serializable

@Serializable
data class Medium(
    val id: Int,
    val idMal: Int?,
    val isAdult: Boolean,
    val genres: Set<String>,
    val countryOfOrigin: String?,
    val averageScore: Int,
    val title: Title,
    val bannerImage: String?,
    val coverImage: CoverImage
) {
    constructor(trending: TrendingQuery.Medium) : this(
        id = trending.id,
        idMal = trending.idMal,
        isAdult = trending.isAdult ?: trending.genresFilterNotNull()?.any {
            AdultContent.Genre.exists(it)
        } ?: false,
        genres = trending.genresFilterNotNull()?.toSet() ?: emptySet(),
        countryOfOrigin = trending.countryOfOrigin?.toString()?.ifBlank { null },
        averageScore = trending.averageScore ?: -1,
        title = Title(
            english = trending.title?.english?.ifBlank { null },
            native = trending.title?.native?.ifBlank { null },
            romaji = trending.title?.romaji?.ifBlank { null },
            userPreferred = trending.title?.userPreferred?.ifBlank { null }
        ),
        bannerImage = trending.bannerImage?.ifBlank { null },
        coverImage = CoverImage(
            color = trending.coverImage?.color?.ifBlank { null },
            medium = trending.coverImage?.medium?.ifBlank { null },
            large = trending.coverImage?.large?.ifBlank { null },
            extraLarge = trending.coverImage?.extraLarge?.ifBlank { null }
        )
    )

    constructor(airing: AiringQuery.Media) : this(
        id = airing.id,
        idMal = airing.idMal,
        isAdult = airing.isAdult ?: airing.genresFilterNotNull()?.any {
            AdultContent.Genre.exists(it)
        } ?: false,
        genres = airing.genresFilterNotNull()?.toSet() ?: emptySet(),
        countryOfOrigin = airing.countryOfOrigin?.toString()?.ifBlank { null },
        averageScore = airing.averageScore ?: -1,
        title = Title(
            english = airing.title?.english?.ifBlank { null },
            native = airing.title?.native?.ifBlank { null },
            romaji = airing.title?.romaji?.ifBlank { null },
            userPreferred = airing.title?.userPreferred?.ifBlank { null }
        ),
        bannerImage = airing.bannerImage?.ifBlank { null },
        coverImage = CoverImage(
            color = airing.coverImage?.color?.ifBlank { null },
            medium = airing.coverImage?.medium?.ifBlank { null },
            large = airing.coverImage?.large?.ifBlank { null },
            extraLarge = airing.coverImage?.extraLarge?.ifBlank { null }
        )
    )

    constructor(season: SeasonQuery.Medium) : this(
        id = season.id,
        idMal = season.idMal,
        isAdult = season.isAdult ?: season.genresFilterNotNull()?.any {
            AdultContent.Genre.exists(it)
        } ?: false,
        genres = season.genresFilterNotNull()?.toSet() ?: emptySet(),
        countryOfOrigin = season.countryOfOrigin?.toString()?.ifBlank { null },
        averageScore = season.averageScore ?: -1,
        title = Title(
            english = season.title?.english?.ifBlank { null },
            native = season.title?.native?.ifBlank { null },
            romaji = season.title?.romaji?.ifBlank { null },
            userPreferred = season.title?.userPreferred?.ifBlank { null }
        ),
        bannerImage = season.bannerImage?.ifBlank { null },
        coverImage = CoverImage(
            color = season.coverImage?.color?.ifBlank { null },
            medium = season.coverImage?.medium?.ifBlank { null },
            large = season.coverImage?.large?.ifBlank { null },
            extraLarge = season.coverImage?.extraLarge?.ifBlank { null }
        )
    )

    @Serializable
    data class Title(
        /**
         * The official english title
         */
        val english: String?,

        /**
         * Official title in its native language
         */
        val native: String?,

        /**
         * The romanization of the native language title
         */
        val romaji: String?,

        /**
         * The currently authenticated users preferred title language. Default romaji for
         * non-authenticated
         */
        val userPreferred: String?
    )

    @Serializable
    data class CoverImage(
        /**
         * Average #hex color of cover image
         */
        val color: String?,

        /**
         * The cover image url of the media at a large size
         */
        val large: String?,

        /**
         * The cover image url of the media at its largest size. If this size isn't available, large
         * will be provided instead.
         */
        val extraLarge: String?,

        /**
         * The cover image url of the media at medium size
         */
        val medium: String?,
    )
}
