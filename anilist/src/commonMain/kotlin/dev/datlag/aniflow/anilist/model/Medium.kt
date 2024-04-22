package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.common.lastMonth
import dev.datlag.aniflow.anilist.type.MediaFormat
import dev.datlag.aniflow.anilist.type.MediaRankType
import dev.datlag.aniflow.anilist.type.MediaStatus
import dev.datlag.aniflow.anilist.type.MediaTrailer
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
open class Medium(
    open val id: Int,
    open val idMal: Int?,
    open val isAdult: Boolean,
    open val genres: Set<String>,
    open val countryOfOrigin: String?,
    open val averageScore: Int,
    open val title: Title,
    open val bannerImage: String?,
    open val coverImage: CoverImage
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

    constructor(query: MediumQuery.Media) : this(
        id = query.id,
        idMal = query.idMal,
        isAdult = query.isAdult ?: query.genresFilterNotNull()?.any {
            AdultContent.Genre.exists(it)
        } ?: false,
        genres = query.genresFilterNotNull()?.toSet() ?: emptySet(),
        countryOfOrigin = query.countryOfOrigin?.toString()?.ifBlank { null },
        averageScore = query.averageScore ?: -1,
        title = Title(
            english = query.title?.english?.ifBlank { null },
            native = query.title?.native?.ifBlank { null },
            romaji = query.title?.romaji?.ifBlank { null },
            userPreferred = query.title?.userPreferred?.ifBlank { null }
        ),
        bannerImage = query.bannerImage?.ifBlank { null },
        coverImage = CoverImage(
            color = query.coverImage?.color?.ifBlank { null },
            medium = query.coverImage?.medium?.ifBlank { null },
            large = query.coverImage?.large?.ifBlank { null },
            extraLarge = query.coverImage?.extraLarge?.ifBlank { null }
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

    @Serializable
    data class Ranking(
        /**
         * The numerical rank of the media
         */
        val rank: Int,

        /**
         * If the ranking is based on all time instead of a season/year
         */
        val allTime: Boolean,

        /**
         * The year the media is ranked within
         */
        val year: Int,

        /**
         * The season the media is ranked within
         */
        val season: Month?,

        /**
         * The type of ranking
         */
        val type: MediaRankType
    ) {
        constructor(ranking: MediumQuery.Ranking) : this(
            rank = ranking.rank,
            allTime = ranking.allTime ?: (ranking.season?.lastMonth() == null && ranking.year == null),
            year = ranking.year ?: -1,
            season = ranking.season?.lastMonth(),
            type = ranking.type
        )
    }

    data class Full(
        override val id: Int,
        override val idMal: Int?,
        val status: MediaStatus,
        val description: String?,
        val episodes: Int,
        val avgEpisodeDurationInMin: Int?,
        val format: MediaFormat,
        override val isAdult: Boolean,
        override val genres: Set<String>,
        override val countryOfOrigin: String?,
        override val averageScore: Int,
        override val title: Title,
        override val bannerImage: String?,
        override val coverImage: CoverImage,
        val nextAiringEpisode: MediumQuery.NextAiringEpisode?,
        val ranking: Set<Ranking>,
        val characters: Set<Character>,
        val entry: Entry?,
        val trailer: Trailer?
    ) : Medium(
        id = id,
        idMal = idMal,
        isAdult = isAdult,
        genres = genres,
        countryOfOrigin = countryOfOrigin,
        averageScore = averageScore,
        title = title,
        bannerImage = bannerImage,
        coverImage = coverImage
    ) {
        constructor(medium: Medium, mediumQuery: MediumQuery.Media) : this(
            id = medium.id,
            idMal = medium.idMal,
            status = mediumQuery.status ?: MediaStatus.UNKNOWN__,
            description = mediumQuery.description?.ifBlank { null },
            episodes = mediumQuery.episodes ?: -1,
            avgEpisodeDurationInMin = mediumQuery.duration ?: -1,
            format = mediumQuery.format ?: MediaFormat.UNKNOWN__,
            isAdult = medium.isAdult,
            genres = medium.genres,
            countryOfOrigin = medium.countryOfOrigin?.ifBlank { null },
            averageScore = medium.averageScore,
            title = medium.title,
            bannerImage = medium.bannerImage?.ifBlank { null },
            coverImage = medium.coverImage,
            nextAiringEpisode = mediumQuery.nextAiringEpisode,
            ranking = mediumQuery.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
            characters = mediumQuery.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.filterNot {
                it.id == 36309 // Narrator
            }?.toSet() ?: emptySet(),
            entry = mediumQuery.mediaListEntry?.let(::Entry),
            trailer = mediumQuery.trailer?.let {
                val site = it.site?.ifBlank { null }
                val thumbnail = it.thumbnail?.ifBlank { null }

                if (site == null || thumbnail == null) {
                    null
                } else {
                    Trailer(
                        id = it.id?.ifBlank { null },
                        site = site,
                        thumbnail = thumbnail
                    )
                }
            }
        )

        constructor(mediumQuery: MediumQuery.Media) : this(
            medium = Medium(mediumQuery),
            mediumQuery = mediumQuery
        )

        data class Entry(
            val score: Double?
        ) {
            constructor(entry: MediumQuery.MediaListEntry) : this(
                score = entry.score
            )
        }

        data class Trailer(
            val id: String?,
            val site: String,
            val thumbnail: String
        ) {
            val website: String = run {
                val prefix = if (site.startsWith("https://", ignoreCase = true) || site.startsWith("http://", ignoreCase = true)) {
                    ""
                } else {
                    "https://"
                }
                val suffix = if (site.substringAfterLast('.', missingDelimiterValue = "").isBlank()) {
                    ".com"
                } else {
                    ""
                }
                "$prefix$site$suffix"
            }

            val isYoutube: Boolean = site.contains("youtu.be", ignoreCase = true)
                    || site.contains("youtube", ignoreCase = true)

            val isDailymotion: Boolean = site.contains("dailymotion", ignoreCase = true)

            private val youtubeVideoId: String? = run {
                val afterVi = thumbnail.substringAfter(
                    delimiter = "vi/",
                    missingDelimiterValue = thumbnail.substringAfter(
                        delimiter = "vi_webp/",
                        missingDelimiterValue = ""
                    )
                ).ifBlank { null } ?: return@run null

                afterVi.substringBefore('/', missingDelimiterValue = "").ifBlank { null }
            }

            private val youtubeVideo = (id ?: youtubeVideoId)?.let {
                "https://youtube.com/watch?v=$it"
            }

            private val dailymotionVideo = id?.let {
                "https://dailymotion.com/video/$it"
            }

            val videoUrl = when {
                isYoutube -> youtubeVideo
                isDailymotion -> dailymotionVideo
                else -> null
            }
        }
    }
}
