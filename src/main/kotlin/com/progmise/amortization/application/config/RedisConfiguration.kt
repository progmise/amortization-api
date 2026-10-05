package com.progmise.amortization.application.config

import com.progmise.amortization.domain.enums.FeatureToggle
import io.github.progmise.commons.infrastructure.Cache
import io.github.progmise.commons.infrastructure.RCache
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.concurrent.TimeUnit

@Configuration
@Profile("!test")
class RedisConfiguration {
    @Bean(destroyMethod = "shutdown")
    fun redissonClient(
        @Value("\${redis.host}") host: String,
        @Value("\${redis.port}") port: Int,
        @Value("\${redis.password:}") password: String,
        @Value("\${redis.ssl:false}") ssl: Boolean,
    ): RedissonClient {
        val config = Config()
        val singleServer = config.useSingleServer().setAddress("redis${if (ssl) "s" else ""}://$host:$port")

        if (password.isNotBlank()) {
            singleServer.password = password
        }

        return Redisson.create(config)
    }

    @Bean
    fun scheduleCache(
        redissonClient: RedissonClient,
        @Value("\${redis.schedule.ttl.seconds}") timeToLive: Long,
        @Value("\${redis.schedule.name}") name: String,
    ): Cache =
        RCache(
            redissonClient,
            name,
            timeToLive,
            TimeUnit.SECONDS,
        ) { FeatureToggle.SCHEDULE_CACHE_ON.isActive() }
}
