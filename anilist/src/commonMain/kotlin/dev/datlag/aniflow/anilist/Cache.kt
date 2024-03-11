package dev.datlag.aniflow.anilist

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import kotlin.time.Duration.Companion.hours

internal object Cache {
    val trendingAnime = InMemoryKache<HomeQuery, HomeQuery.Data>(
        maxSize = 5L * 1024 * 1024,
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 2.hours
    }
}