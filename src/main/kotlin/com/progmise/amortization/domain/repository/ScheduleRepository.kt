package com.progmise.amortization.domain.repository

import com.progmise.amortization.domain.entity.Schedule

interface ScheduleRepository {
    fun save(schedule: Schedule): Schedule

    fun findById(id: String): Schedule?

    fun findAll(
        offset: Int,
        limit: Int,
    ): Pair<List<Schedule>, Long>
}
