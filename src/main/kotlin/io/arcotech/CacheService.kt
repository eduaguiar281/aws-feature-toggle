package io.arcotech

import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

data class CacheEntry(val value: String, val timestamp: Long)

class CacheService {
    private val cache = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(2, TimeUnit.MINUTES)
        .build<String, CacheEntry>()
    private val mutex = Mutex()

    suspend fun getValue(key: String): CacheEntry? {
        return mutex.withLock {
            cache.getIfPresent(key)
        }
    }

    suspend fun saveValue(value: String, key: String) {
        val cacheEntry = CacheEntry(value, System.currentTimeMillis())
        mutex.withLock {
            cache.put(key, cacheEntry)
        }
    }
}