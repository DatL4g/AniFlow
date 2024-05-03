package dev.datlag.aniflow.anilist

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.aniflow.anilist.model.Character
import dev.datlag.aniflow.anilist.model.Medium
import dev.datlag.tooling.async.suspendCatching
import kotlin.time.Duration.Companion.hours

internal object Cache {
    private val medium = InMemoryKache<MediumQuery, Medium>(
        maxSize = 10L * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 2.hours
    }

    private val character = InMemoryKache<CharacterQuery, Character>(
        maxSize = 5L * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
        expireAfterWriteDuration = 2.hours
    }

    suspend fun getMedium(key: MediumQuery): Medium? {
        return suspendCatching {
            medium.getIfAvailable(key)
        }.getOrNull()
    }

    suspend fun setMedium(key: MediumQuery, data: Medium): Medium {
        return suspendCatching {
            medium.put(key, data)
        }.getOrNull() ?: data
    }

    suspend fun getCharacter(key: CharacterQuery) : Character? {
        return suspendCatching {
            character.getIfAvailable(key)
        }.getOrNull()
    }

    suspend fun setCharacter(key: CharacterQuery, data: Character): Character {
        return suspendCatching {
            character.put(key, data)
        }.getOrNull() ?: data
    }
}