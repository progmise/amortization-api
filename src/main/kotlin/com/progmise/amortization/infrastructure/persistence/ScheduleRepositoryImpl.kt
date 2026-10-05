package com.progmise.amortization.infrastructure.persistence

import com.progmise.amortization.domain.entity.Schedule
import com.progmise.amortization.domain.repository.ScheduleRepository
import com.progmise.amortization.infrastructure.mapper.ScheduleEntityMapper
import com.progmise.amortization.infrastructure.persistence.jpa.ScheduleJpaRepository
import io.github.progmise.commons.infrastructure.Cache
import org.springframework.data.domain.PageRequest
import java.util.UUID

class ScheduleRepositoryImpl(
    private val scheduleCache: Cache,
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val mapper: ScheduleEntityMapper,
) : ScheduleRepository {
    override fun save(schedule: Schedule): Schedule {
        val withId = schedule.id?.let { schedule } ?: schedule.copy(id = UUID.randomUUID().toString())
        val entity = scheduleJpaRepository.save(mapper.toEntity(withId))

        return mapper.toDomain(entity)
    }

    override fun findById(id: String): Schedule? {
        val fromCache = scheduleCache.get(id, Schedule::class.java)

        return fromCache
            ?: scheduleJpaRepository.findById(id).map { mapper.toDomain(it) }.orElse(null)?.also {
                scheduleCache.fastPut(id, it)
            }
    }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): Pair<List<Schedule>, Long> {
        val page = scheduleJpaRepository.findAll(PageRequest.of(offset / limit, limit))

        return page.content.map { mapper.toDomain(it) } to page.totalElements
    }
}
