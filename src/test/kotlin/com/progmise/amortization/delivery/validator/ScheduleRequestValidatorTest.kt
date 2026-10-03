package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ScheduleRequestValidatorTest {
    private val validator = ScheduleRequestValidator()

    @Test
    fun `valid request produces no exceptions`() {
        val exceptions = validator.validate(validRequest(), ArrayList())

        assertTrue(exceptions.isEmpty())
    }

    @Test
    fun `empty request reports all required fields`() {
        val exceptions = validator.validate(CreateScheduleRequest(), ArrayList())

        assertEquals(5, exceptions.size)
        assertTrue(exceptions.any { it.code == "required.field.principal" })
        assertTrue(exceptions.any { it.code == "required.field.annualRate" })
        assertTrue(exceptions.any { it.code == "required.field.installments" })
        assertTrue(exceptions.any { it.code == "required.field.system" })
        assertTrue(exceptions.any { it.code == "required.field.startDate" })
    }

    @Test
    fun `invalid values are reported with invalid value codes`() {
        val request =
            validRequest().copy(
                principal = "abc",
                annualRate = "x%",
                installments = "12.5",
                system = "ITALIAN",
                startDate = "01-02-2026",
            )

        val exceptions = validator.validate(request, ArrayList())

        assertTrue(exceptions.any { it.code == "invalid.value.principal" })
        assertTrue(exceptions.any { it.code == "invalid.value.annualRate" })
        assertTrue(exceptions.any { it.code == "invalid.value.installments" })
        assertTrue(exceptions.any { it.code == "invalid.value.system" })
        assertTrue(exceptions.any { it.code == "invalid.value.startDate" })
    }

    @Test
    fun `zero installments is rejected`() {
        val exceptions = validator.validate(validRequest().copy(installments = "0"), ArrayList())

        assertTrue(exceptions.any { it.code == "invalid.value.installments" })
    }

    @Test
    fun `nested field names are appended to codes`() {
        val exceptions = validator.validate(CreateScheduleRequest(), listOf("body"))

        assertTrue(exceptions.any { it.code == "required.field.body.principal" })
    }

    private fun validRequest() =
        CreateScheduleRequest(
            principal = "100000.50",
            annualRate = "45.5",
            installments = "12",
            system = "FRENCH",
            startDate = "2026-02-01",
        )
}
