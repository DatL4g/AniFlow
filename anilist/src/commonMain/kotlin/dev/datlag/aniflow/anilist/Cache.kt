package dev.datlag.aniflow.anilist

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import kotlin.time.Duration.Companion.hours

internal object Cache {
    val trendingAnime = InMemoryKache<TrendingQuery, TrendingQuery.Data>(
        maxSize = 5L * 1024 * 1024,
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 2.hours
    }

    val airing = InMemoryKache<AiringQuery, AiringQuery.Data>(
        maxSize = 5L * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 1.hours
    }

    val popularSeason = InMemoryKache<SeasonQuery, SeasonQuery.Data>(
        maxSize = 5L * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 2.hours
    }
}