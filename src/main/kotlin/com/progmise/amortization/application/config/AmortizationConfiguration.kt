package com.progmise.amortization.application.config

import com.progmise.amortization.domain.repository.ScheduleRepository
import com.progmise.amortization.infrastructure.mapper.ScheduleEntityMapper
import com.progmise.amortization.infrastructure.persistence.ScheduleRepositoryImpl
import com.progmise.amortization.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.github.progmise.commons.infrastructure.Cache
import io.github.progmise.commons.infrastructure.NoOpCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class AmortizationConfiguration {
    @Bean
    fun scheduleRepository(
        scheduleCache: Cache,
        scheduleJpaRepository: ScheduleJpaRepository,
        scheduleEntityMapper: ScheduleEntityMapper,
    ): ScheduleRepository = ScheduleRepositoryImpl(scheduleCache, scheduleJpaRepository, scheduleEntityMapper)

    @Bean
    @Profile("test")
    fun testScheduleCache(): Cache = NoOpCache()
}
