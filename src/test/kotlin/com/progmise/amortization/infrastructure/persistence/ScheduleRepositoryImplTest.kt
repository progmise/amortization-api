package com.progmise.amortization.infrastructure.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.progmise.amortization.domain.entity.Schedule
import com.progmise.amortization.domain.entity.ScheduleCriteria
import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.amortization.domain.service.AmortizationCalculator
import com.progmise.amortization.infrastructure.cache.Cache
import com.progmise.amortization.infrastructure.mapper.ScheduleEntityMapper
import com.progmise.amortization.infrastructure.persistence.entity.ScheduleEntity
import com.progmise.amortization.infrastructure.persistence.jpa.ScheduleJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

class ScheduleRepositoryImplTest {
    private val cache: Cache = mock()
    private val jpaRepository: ScheduleJpaRepository = mock()
    private val mapper = ScheduleEntityMapper()
    private val repository = ScheduleRepositoryImpl(cache, jpaRepository, mapper)

    private val schedule =
        AmortizationCalculator.calculate(
            ScheduleCriteria(
                principal = BigDecimal("1200"),
                annualRate = BigDecimal("12"),
                installments = 12,
                system = AmortizationSystem.GERMAN,
                startDate = LocalDate.of(2026, 2, 1),
            ),
            id = "5f7b2f7e-9a6b-4f0e-a3c1-8d6e5f4a3b2c",
        )

    @Test
    fun `findById returns cached schedule without hitting the database`() {
        whenever(cache.getObject(eq(schedule.id!!), any<TypeReference<Schedule>>()))
            .thenReturn(schedule)

        val result = repository.findById(schedule.id!!)

        assertEquals(schedule, result)
        verify(jpaRepository, never()).findById(any<String>())
    }

    @Test
    fun `findById falls back to the database and populates the cache`() {
        whenever(cache.getObject(eq(schedule.id!!), any<TypeReference<Schedule>>()))
            .thenReturn(null)
        whenever(jpaRepository.findById(schedule.id!!)).thenReturn(Optional.of(mapper.toEntity(schedule)))

        val result = repository.findById(schedule.id!!)

        assertNotNull(result)
        assertEquals(schedule.id, result!!.id)
        verify(cache).fastPut(schedule.id!!, result)
    }

    @Test
    fun `findById returns null when the schedule does not exist`() {
        whenever(cache.getObject(eq("missing"), any<TypeReference<Schedule>>()))
            .thenReturn(null)
        whenever(jpaRepository.findById("missing")).thenReturn(Optional.empty())

        assertNull(repository.findById("missing"))
    }

    @Test
    fun `save assigns an id when the schedule does not have one`() {
        val entity = mapper.toEntity(schedule)
        whenever(jpaRepository.save(any<ScheduleEntity>())).thenReturn(entity)

        val result = repository.save(schedule.copy(id = null))

        assertNotNull(result.id)
        verify(jpaRepository).save(any<ScheduleEntity>())
    }
}
