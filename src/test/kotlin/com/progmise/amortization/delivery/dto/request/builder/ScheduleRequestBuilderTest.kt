package com.progmise.amortization.delivery.dto.request.builder

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.delivery.validator.ScheduleRequestValidator
import com.progmise.amortization.domain.enums.AmortizationSystem
import io.github.progmise.utils.exception.BadRequestException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

class ScheduleRequestBuilderTest {
    private val builder = ScheduleRequestBuilder(validator = ScheduleRequestValidator())

    @Test
    fun `build returns typed criteria for a valid request`() {
        val criteria =
            builder.build(
                CreateScheduleRequest(
                    principal = "50000.25",
                    annualRate = "30",
                    installments = "24",
                    system = "german",
                    startDate = "2026-03-15",
                ),
            )

        assertEquals(BigDecimal("50000.25"), criteria.principal)
        assertEquals(BigDecimal("30"), criteria.annualRate)
        assertEquals(24, criteria.installments)
        assertEquals(AmortizationSystem.GERMAN, criteria.system)
        assertEquals(LocalDate.of(2026, 3, 15), criteria.startDate)
    }

    @Test
    fun `build throws BadRequestException with all violations`() {
        val exception =
            assertThrows<BadRequestException> {
                builder.build(CreateScheduleRequest(principal = "abc"))
            }

        assertEquals("error.body", exception.errorCodeGeneral)
        assertEquals(5, exception.exceptions.size)
    }
}
