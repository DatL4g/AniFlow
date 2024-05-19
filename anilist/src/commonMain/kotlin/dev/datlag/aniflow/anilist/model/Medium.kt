package dev.datlag.aniflow.anilist.model

import dev.datlag.aniflow.anilist.*
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.common.lastMonth
import dev.datlag.aniflow.anilist.common.toLocalDate
import dev.datlag.aniflow.anilist.type.*
import dev.datlag.aniflow.model.ifValue
import dev.datlag.aniflow.model.toInt
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Medium(
    val id: Int,
    val idMal: Int? = null,
    val type: MediaType = MediaType.UNKNOWN__,
    val status: MediaStatus = MediaStatus.UNKNOWN__,
    val description: String? = null,
    private val _episodes: Int = -1,
    val avgEpisodeDurationInMin: Int = -1,
    val format: MediaFormat = MediaFormat.UNKNOWN__,
    private val _isAdult: Boolean = false,
    val genres: Set<String> = emptySet(),
    val countryOfOrigin: String? = null,
    val averageScore: Int = -1,
    val title: Title = Title(
        english = null,
        native = null,
        romaji = null,
        userPreferred = null
    ),
    val bannerImage: String? = null,
    val coverImage: CoverImage = CoverImage(
        medium = null,
        large = null,
        extraLarge = null,
        color = null
    ),
    val nextAiringEpisode: NextAiring? = null,
    val ranking: Set<Ranking> = emptySet(),
    private val _characters: Set<Character> = emptySet(),
    val entry: Entry? = null,
    val trailer: Trailer? = null,
    val isFavorite: Boolean = false,
    private val _isFavoriteBlocked: Boolean = true,
    val siteUrl: String = "$SITE_URL$id",
    val chapters: Int = -1,
    val volumes: Int = -1,
    val startDate: LocalDate? = null
) {
    constructor(trending: TrendingQuery.Medium) : this(
        id = trending.id,
        idMal = trending.idMal,
        type = trending.type ?: MediaType.UNKNOWN__,
        status = trending.status ?: MediaStatus.UNKNOWN__,
        description = trending.description?.ifBlank { null },
        _episodes = trending.episodes ?: -1,
        avgEpisodeDurationInMin = trending.duration ?: -1,
        format = trending.format ?: MediaFormat.UNKNOWN__,
        _isAdult = trending.isAdult ?: false,
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
        ),
        nextAiringEpisode = trending.nextAiringEpisode?.let(::NextAiring),
        ranking = trending.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
        _characters = trending.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.toSet() ?: emptySet(),
        entry = trending.mediaListEntry?.let(::Entry),
        trailer = trending.trailer?.let {
            val site = it.site?.ifBlank { null }
            val thumbnail = it.thumbnail?.ifBlank { null }

            if (site == null || thumbnail == null) {
                null
            } else {
                Trailer(
                    id = it.id?.ifBlank { null },
                    site = site,
                    thumbnail = thumbnail,
                )
            }
        },
        isFavorite = trending.isFavourite,
        _isFavoriteBlocked = trending.isFavouriteBlocked,
        siteUrl = trending.siteUrl?.ifBlank { null } ?: "$SITE_URL${trending.id}",
        chapters = trending.chapters ?: -1,
        volumes = trending.volumes ?: -1,
        startDate = trending.startDate?.toLocalDate()
    )

    constructor(airing: AiringQuery.Media) : this(
        id = airing.id,
        idMal = airing.idMal,
        type = airing.type ?: MediaType.UNKNOWN__,
        status = airing.status ?: MediaStatus.UNKNOWN__,
        description = airing.description?.ifBlank { null },
        _episodes = airing.episodes ?: -1,
        avgEpisodeDurationInMin = airing.duration ?: -1,
        format = airing.format ?: MediaFormat.UNKNOWN__,
        _isAdult = airing.isAdult ?: false,
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
        ),
        nextAiringEpisode = airing.nextAiringEpisode?.let(::NextAiring),
        ranking = airing.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
        _characters = airing.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.toSet() ?: emptySet(),
        entry = airing.mediaListEntry?.let(::Entry),
        trailer = airing.trailer?.let {
            val site = it.site?.ifBlank { null }
            val thumbnail = it.thumbnail?.ifBlank { null }

            if (site == null || thumbnail == null) {
                null
            } else {
                Trailer(
                    id = it.id?.ifBlank { null },
                    site = site,
                    thumbnail = thumbnail,
                )
            }
        },
        isFavorite = airing.isFavourite,
        _isFavoriteBlocked = airing.isFavouriteBlocked,
        siteUrl = airing.siteUrl?.ifBlank { null } ?: "$SITE_URL${airing.id}",
        chapters = airing.chapters ?: -1,
        volumes = airing.volumes ?: -1,
        startDate = airing.startDate?.toLocalDate()
    )

    constructor(season: SeasonQuery.Medium) : this(
        id = season.id,
        idMal = season.idMal,
        type = season.type ?: MediaType.UNKNOWN__,
        status = season.status ?: MediaStatus.UNKNOWN__,
        description = season.description?.ifBlank { null },
        _episodes = season.episodes ?: -1,
        avgEpisodeDurationInMin = season.duration ?: -1,
        format = season.format ?: MediaFormat.UNKNOWN__,
        _isAdult = season.isAdult ?: false,
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
        ),
        nextAiringEpisode = season.nextAiringEpisode?.let(::NextAiring),
        ranking = season.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
        _characters = season.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.toSet() ?: emptySet(),
        entry = season.mediaListEntry?.let(::Entry),
        trailer = season.trailer?.let {
            val site = it.site?.ifBlank { null }
            val thumbnail = it.thumbnail?.ifBlank { null }

            if (site == null || thumbnail == null) {
                null
            } else {
                Trailer(
                    id = it.id?.ifBlank { null },
                    site = site,
                    thumbnail = thumbnail,
                )
            }
        },
        isFavorite = season.isFavourite,
        _isFavoriteBlocked = season.isFavouriteBlocked,
        siteUrl = season.siteUrl?.ifBlank { null } ?: "$SITE_URL${season.id}",
        chapters = season.chapters ?: -1,
        volumes = season.volumes ?: -1,
        startDate = season.startDate?.toLocalDate()
    )

    constructor(query: MediumQuery.Media) : this(
        id = query.id,
        idMal = query.idMal,
        type = query.type ?: MediaType.UNKNOWN__,
        status = query.status ?: MediaStatus.UNKNOWN__,
        description = query.description?.ifBlank { null },
        _episodes = query.episodes ?: -1,
        avgEpisodeDurationInMin = query.duration ?: -1,
        format = query.format ?: MediaFormat.UNKNOWN__,
        _isAdult = query.isAdult ?: false,
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
        ),
        nextAiringEpisode = query.nextAiringEpisode?.let(::NextAiring),
        ranking = query.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
        _characters = query.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.toSet() ?: emptySet(),
        entry = query.mediaListEntry?.let(::Entry),
        trailer = query.trailer?.let {
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
        },
        isFavorite = query.isFavourite,
        _isFavoriteBlocked = query.isFavouriteBlocked,
        siteUrl = query.siteUrl?.ifBlank { null } ?: "$SITE_URL${query.id}",
        chapters = query.chapters ?: -1,
        volumes = query.volumes ?: -1,
        startDate = query.startDate?.toLocalDate()
    )

    constructor(media: ListQuery.Media, list: ListQuery.MediaList?) : this(
        id = media.id,
        idMal = media.idMal,
        type = media.type ?: MediaType.UNKNOWN__,
        status = media.status ?: MediaStatus.UNKNOWN__,
        description = media.description?.ifBlank { null },
        _episodes = media.episodes ?: -1,
        avgEpisodeDurationInMin = media.duration ?: -1,
        format = media.format ?: MediaFormat.UNKNOWN__,
        _isAdult = media.isAdult ?: false,
        genres = media.genresFilterNotNull()?.toSet() ?: emptySet(),
        countryOfOrigin = media.countryOfOrigin?.toString()?.ifBlank { null },
        averageScore = media.averageScore ?: -1,
        title = Title(
            english = media.title?.english?.ifBlank { null },
            native = media.title?.native?.ifBlank { null },
            romaji = media.title?.romaji?.ifBlank { null },
            userPreferred = media.title?.userPreferred?.ifBlank { null }
        ),
        bannerImage = media.bannerImage?.ifBlank { null },
        coverImage = CoverImage(
            color = media.coverImage?.color?.ifBlank { null },
            medium = media.coverImage?.medium?.ifBlank { null },
            large = media.coverImage?.large?.ifBlank { null },
            extraLarge = media.coverImage?.extraLarge?.ifBlank { null }
        ),
        nextAiringEpisode = media.nextAiringEpisode?.let(::NextAiring),
        ranking = media.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
        _characters = media.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.toSet() ?: emptySet(),
        entry = list?.let(::Entry),
        trailer = media.trailer?.let {
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
        },
        isFavorite = media.isFavourite,
        _isFavoriteBlocked = media.isFavouriteBlocked,
        siteUrl = media.siteUrl?.ifBlank { null } ?: "$SITE_URL${media.id}",
        chapters = media.chapters ?: -1,
        volumes = media.volumes ?: -1,
        startDate = media.startDate?.toLocalDate()
    )

    constructor(search: SearchQuery.Medium) : this(
        id = search.id,
        idMal = search.idMal,
        type = search.type ?: MediaType.UNKNOWN__,
        status = search.status ?: MediaStatus.UNKNOWN__,
        description = search.description?.ifBlank { null },
        _episodes = search.episodes ?: -1,
        avgEpisodeDurationInMin = search.duration ?: -1,
        format = search.format ?: MediaFormat.UNKNOWN__,
        _isAdult = search.isAdult ?: false,
        genres = search.genresFilterNotNull()?.toSet() ?: emptySet(),
        countryOfOrigin = search.countryOfOrigin?.toString()?.ifBlank { null },
        averageScore = search.averageScore ?: -1,
        title = Title(
            english = search.title?.english?.ifBlank { null },
            native = search.title?.native?.ifBlank { null },
            romaji = search.title?.romaji?.ifBlank { null },
            userPreferred = search.title?.userPreferred?.ifBlank { null }
        ),
        bannerImage = search.bannerImage?.ifBlank { null },
        coverImage = CoverImage(
            color = search.coverImage?.color?.ifBlank { null },
            medium = search.coverImage?.medium?.ifBlank { null },
            large = search.coverImage?.large?.ifBlank { null },
            extraLarge = search.coverImage?.extraLarge?.ifBlank { null }
        ),
        nextAiringEpisode = search.nextAiringEpisode?.let(::NextAiring),
        ranking = search.rankingsFilterNotNull()?.map(::Ranking)?.toSet() ?: emptySet(),
        _characters = search.characters?.nodesFilterNotNull()?.mapNotNull(Character::invoke)?.toSet() ?: emptySet(),
        entry = search.mediaListEntry?.let(::Entry),
        trailer = search.trailer?.let {
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
        },
        isFavorite = search.isFavourite,
        _isFavoriteBlocked = search.isFavouriteBlocked,
        siteUrl = search.siteUrl?.ifBlank { null } ?: "$SITE_URL${search.id}",
        chapters = search.chapters ?: -1,
        volumes = search.volumes ?: -1,
        startDate = search.startDate?.toLocalDate()
    )

    @Transient
    val isAdult: Boolean = _isAdult || genres.any {
        AdultContent.Genre.exists(it)
    }

    @Transient
    val characters: Set<Character> = _characters.filterNot { it.id == 36309 }.toSet()

    @Transient
    val isFavoriteBlocked: Boolean = _isFavoriteBlocked || type == MediaType.UNKNOWN__

    val episodes: Int
        get() = _episodes.ifValue(-1) {
            nextAiringEpisode?.let {
                if (Instant.fromEpochSeconds(it.airingAt.toLong()) <= Clock.System.now()) {
                    it.episodes
                } else {
                    it.episodes - 1
                }
            } ?: -1
        }

    val episodesOrChapters: Int
        get() = when (type) {
            MediaType.MANGA -> chapters
            else -> episodes
        }

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

        constructor(ranking: TrendingQuery.Ranking) : this(
            rank = ranking.rank,
            allTime = ranking.allTime ?: (ranking.season?.lastMonth() == null && ranking.year == null),
            year = ranking.year ?: -1,
            season = ranking.season?.lastMonth(),
            type = ranking.type
        )

        constructor(ranking: AiringQuery.Ranking) : this(
            rank = ranking.rank,
            allTime = ranking.allTime ?: (ranking.season?.lastMonth() == null && ranking.year == null),
            year = ranking.year ?: -1,
            season = ranking.season?.lastMonth(),
            type = ranking.type
        )

        constructor(ranking: SeasonQuery.Ranking) : this(
            rank = ranking.rank,
            allTime = ranking.allTime ?: (ranking.season?.lastMonth() == null && ranking.year == null),
            year = ranking.year ?: -1,
            season = ranking.season?.lastMonth(),
            type = ranking.type
        )

        constructor(ranking: ListQuery.Ranking) : this(
            rank = ranking.rank,
            allTime = ranking.allTime ?: (ranking.season?.lastMonth() == null && ranking.year == null),
            year = ranking.year ?: -1,
            season = ranking.season?.lastMonth(),
            type = ranking.type
        )

        constructor(ranking: SearchQuery.Ranking) : this(
            rank = ranking.rank,
            allTime = ranking.allTime ?: (ranking.season?.lastMonth() == null && ranking.year == null),
            year = ranking.year ?: -1,
            season = ranking.season?.lastMonth(),
            type = ranking.type
        )
    }

    @Serializable
    data class Entry(
        val score: Double?,
        val status: MediaListStatus,
        val progress: Int?,
        val repeatCount: Int?,
        val startDate: LocalDate?
    ) {
        constructor(entry: MediumQuery.MediaListEntry) : this(
            score = entry.score,
            status = entry.status ?: MediaListStatus.UNKNOWN__,
            progress = entry.progress,
            repeatCount = entry.repeat,
            startDate = entry.startedAt?.toLocalDate()
        )

        constructor(entry: TrendingQuery.MediaListEntry) : this(
            score = entry.score,
            status = entry.status ?: MediaListStatus.UNKNOWN__,
            progress = entry.progress,
            repeatCount = entry.repeat,
            startDate = entry.startedAt?.toLocalDate()
        )

        constructor(entry: AiringQuery.MediaListEntry) : this(
            score = entry.score,
            status = entry.status ?: MediaListStatus.UNKNOWN__,
            progress = entry.progress,
            repeatCount = entry.repeat,
            startDate = entry.startedAt?.toLocalDate()
        )

        constructor(entry: SeasonQuery.MediaListEntry) : this(
            score = entry.score,
            status = entry.status ?: MediaListStatus.UNKNOWN__,
            progress = entry.progress,
            repeatCount = entry.repeat,
            startDate = entry.startedAt?.toLocalDate()
        )

        constructor(entry: ListQuery.MediaList) : this(
            score = entry.score,
            status = entry.status ?: MediaListStatus.UNKNOWN__,
            progress = entry.progress,
            repeatCount = entry.repeat,
            startDate = entry.startedAt?.toLocalDate()
        )

        constructor(entry: SearchQuery.MediaListEntry) : this(
            score = entry.score,
            status = entry.status ?: MediaListStatus.UNKNOWN__,
            progress = entry.progress,
            repeatCount = entry.repeat,
            startDate = entry.startedAt?.toLocalDate()
        )
    }

    @Serializable
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

    @Serializable
    data class NextAiring(
        /**
         * The airing episode number
         */
        val episodes: Int,

        /**
         * The time the episode airs at
         */
        val airingAt: Int
    ) {
        constructor(nextAiringEpisode: TrendingQuery.NextAiringEpisode) : this(
            episodes = nextAiringEpisode.episode,
            airingAt = nextAiringEpisode.airingAt
        )

        constructor(nextAiringEpisode: MediumQuery.NextAiringEpisode) : this(
            episodes = nextAiringEpisode.episode,
            airingAt = nextAiringEpisode.airingAt
        )

        constructor(nextAiringEpisode: AiringQuery.NextAiringEpisode) : this(
            episodes = nextAiringEpisode.episode,
            airingAt = nextAiringEpisode.airingAt
        )

        constructor(nextAiringEpisode: SeasonQuery.NextAiringEpisode) : this(
            episodes = nextAiringEpisode.episode,
            airingAt = nextAiringEpisode.airingAt
        )

        constructor(nextAiringEpisode: ListQuery.NextAiringEpisode) : this(
            episodes = nextAiringEpisode.episode,
            airingAt = nextAiringEpisode.airingAt
        )

        constructor(nextAiringEpisode: SearchQuery.NextAiringEpisode) : this(
            episodes = nextAiringEpisode.episode,
            airingAt = nextAiringEpisode.airingAt
        )
    }

    companion object {
        private const val SITE_URL = "https://anilist.co/anime/"
    }
}
