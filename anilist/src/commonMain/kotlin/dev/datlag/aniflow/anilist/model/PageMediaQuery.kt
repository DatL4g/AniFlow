package dev.datlag.aniflow.anilist.model

import com.apollographql.apollo3.api.Optional
import dev.datlag.aniflow.anilist.AdultContent
import dev.datlag.aniflow.anilist.common.nextSeason
import dev.datlag.aniflow.anilist.common.presentAsList
import dev.datlag.aniflow.anilist.common.presentIfNot
import dev.datlag.aniflow.anilist.common.presentMediaSeason
import dev.datlag.aniflow.anilist.common.presentMediaType
import dev.datlag.aniflow.anilist.type.MediaSeason
import dev.datlag.aniflow.anilist.type.MediaSort
import dev.datlag.aniflow.anilist.type.MediaType
import dev.datlag.tooling.safeSubList
import dev.datlag.tooling.safeSubSet
import kotlinx.datetime.Clock
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
        val wantedGenres: Collection<String>,
        val preventIds: Collection<Int>,
        val type: MediaType,
        val nsfw: Boolean
    ) : PageMediaQuery {

        constructor(
            nsfw: Boolean,
            collection: Collection<Medium>,
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
            },
            preventIds = collection.map { it.id },
            type = type,
            nsfw = nsfw
        )

        override fun toGraphQL() = PageMediaGraphQL(
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            sort = Optional.presentAsList(MediaSort.TRENDING_DESC),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            wantedGenres = Optional.present(wantedGenres.toList()),
            preventIds = Optional.present(preventIds.toList()),
            onList = Optional.present(false),
            statusVersion = 2,
            html = true
        )
    }

    data class Season(
        val season: MediaSeason,
        val type: MediaType,
        val nsfw: Boolean
    ) : PageMediaQuery {
        override fun toGraphQL() = PageMediaGraphQL(
            season = Optional.presentMediaSeason(season),
            adultContent = Optional.presentIfNot(nsfw),
            type = Optional.presentMediaType(type),
            preventGenres = Optional.presentIfNot(nsfw, AdultContent.Genre.allTags),
            statusVersion = 2,
            html = true
        )
    }
}