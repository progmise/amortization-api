package com.progmise.amortization.infrastructure.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.progmise.amortization.utils.logger
import org.redisson.api.RMapCache
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

class RedisCache(
    redissonClient: RedissonClient,
    redisCollection: String,
    private val timeToLive: Long,
    private val unit: TimeUnit,
    private val objectMapper: ObjectMapper,
    private val isRedisEnabled: () -> Boolean = { true },
) : Cache {
    private val log = logger()
    private val map: RMapCache<String, String> = redissonClient.getMapCache(redisCollection)

    override fun <T> getObject(
        key: String,
        typeReference: TypeReference<T>,
    ): T? {
        if (isEnabled().not()) {
            return null
        }

        return try {
            map[key]?.let { objectMapper.readValue(it, typeReference) }
        } catch (e: Exception) {
            log.warn("Unable to read key {} from cache: {}", key, e.message)
            null
        }
    }

    override fun fastPut(
        key: String,
        value: Any,
    ) {
        if (isEnabled().not()) {
            return
        }

        try {
            map.fastPut(key, objectMapper.writeValueAsString(value), timeToLive, unit)
        } catch (e: Exception) {
            log.warn("Unable to write key {} to cache: {}", key, e.message)
        }
    }

    override fun delete(key: String) {
        try {
            map.fastRemove(key)
        } catch (e: Exception) {
            log.warn("Unable to delete key {} from cache: {}", key, e.message)
        }
    }

    private fun isEnabled(): Boolean =
        try {
            isRedisEnabled()
        } catch (e: Exception) {
            log.warn("Unable to resolve cache feature toggle: {}", e.message)
            false
        }
}
