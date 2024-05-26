package dev.datlag.aniflow.anilist.model

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.common.nextSeason
import dev.datlag.aniflow.anilist.common.presentAsList
import dev.datlag.aniflow.anilist.common.presentIfNot
import dev.datlag.aniflow.anilist.common.presentMediaSeason
import dev.datlag.aniflow.anilist.common.presentMediaType
import dev.datlag.aniflow.anilist.common.year
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.tooling.safeSubList
import dev.datlag.tooling.safeSubSet
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlin.time.Duration.Companion.seconds
import dev.datlag.aniflow.anilist.PageMediaQuery as PageMediaGraphQL

sealed interface PageMediaQuery {
    fun toGraphQL(): PageMediaGraphQL

    data class Trending(
        val type: MediaType,
        val nsfw: Boolean
    ) : PageMediaQuery {
        override fun toGraphQL() = PageMediaGraphQL(
            perPage = Optional.present(20),
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            sort = Optional.presentAsList(MediaSort.TRENDING_DESC),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            statusVersion = 2,
            html = true
        )
    }

    data class PopularSeason(
        val type: MediaType,
        val nsfw: Boolean
    ) : PageMediaQuery {
        override fun toGraphQL() = PageMediaGraphQL(
            perPage = Optional.present(20),
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            sort = Optional.presentAsList(MediaSort.POPULARITY_DESC),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            year = Optional.absent(),
            season = Optional.absent(),
            statusVersion = 2,
            html = true
        )
    }

    data class PopularNextSeason(
        val type: MediaType,
        val nsfw: Boolean,
        val season: MediaSeason,
        val year: Int
    ) : PageMediaQuery {

        constructor(
            type: MediaType,
            nsfw: Boolean,
            nextSeason: Pair<MediaSeason, Int> = Clock.System.now().nextSeason
        ) : this(
            type = type,
            nsfw = nsfw,
            season = nextSeason.first,
            year = nextSeason.second
        )

        override fun toGraphQL() = PageMediaGraphQL(
            perPage = Optional.present(20),
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            sort = Optional.presentAsList(MediaSort.POPULARITY_DESC),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            year = Optional.present(year),
            season = Optional.presentMediaSeason(season),
            statusVersion = 2,
            html = true
        )
    }

    data class Search(
        val query: String,
        val type: MediaType,
        val nsfw: Boolean
    ) : PageMediaQuery {
        override fun toGraphQL() = PageMediaGraphQL(
            query = Optional.present(query),
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            sort = Optional.presentAsList(MediaSort.SEARCH_MATCH, MediaSort.POPULARITY_DESC),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            statusVersion = 2,
            html = true
        )
    }

    data class Recommendation(
        val wantedGenres: ImmutableCollection<String>,
        val preventIds: ImmutableCollection<Int>,
        val type: MediaType,
        val nsfw: Boolean
    ) : PageMediaQuery {

        constructor(
            nsfw: Boolean,
            collection: ImmutableCollection<Medium>,
            type: MediaType = collection.let { c ->
                val allTypes = c.map {
                    it.type
                }.toSet().filterNot {
                    it == MediaType.UNKNOWN__
                }

                val hasAnime = allTypes.any { it == MediaType.ANIME }
                val hasManga = allTypes.any { it == MediaType.MANGA }

                if (hasAnime && hasManga) {
                    MediaType.UNKNOWN__
                } else if (hasAnime) {
                    MediaType.ANIME
                } else {
                    MediaType.MANGA
                }
            }
        ) : this(
            wantedGenres = collection.let {
                val allGenres = it.flatMap { m ->
                    m.genres.toList()
                }.toMutableList()

                if (!nsfw) {
                    AdultContent.Genre.allTags.forEach { g ->
                        allGenres.remove(g)
                    }
                }

                allGenres.groupingBy { g -> g }.eachCount().toList().sortedByDescending { p ->
                    p.second
                }.safeSubSet(0, 5).toMap().keys.safeSubSet(0, 5)
            }.toImmutableSet(),
            preventIds = collection.map { it.id }.toImmutableSet(),
            type = type,
            nsfw = nsfw
        )

        override fun toGraphQL() = PageMediaGraphQL(
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            sort = Optional.presentAsList(MediaSort.TRENDING_DESC),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            wantedGenres = Optional.presentAsList(*wantedGenres.toTypedArray()),
            preventIds = Optional.presentAsList(*preventIds.toTypedArray()),
            onList = Optional.present(false),
            statusVersion = 2,
            html = true
        )
    }

    data class Season(
        val season: MediaSeason,
        val year: Int = Clock.System.now().minus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault()).year,
        val nsfw: Boolean
    ) : PageMediaQuery {
        override fun toGraphQL() = PageMediaGraphQL(
            season = Optional.presentMediaSeason(season),
            year = Optional.present(year),
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(MediaType.ANIME),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            statusVersion = 2,
            html = true
        )
    }
}