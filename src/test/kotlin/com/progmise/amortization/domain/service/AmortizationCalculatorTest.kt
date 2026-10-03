package com.progmise.amortization.domain.service

import com.progmise.amortization.domain.entity.ScheduleCriteria
import com.progmise.amortization.domain.enums.AmortizationSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class AmortizationCalculatorTest {
    private val startDate = LocalDate.of(2026, 2, 1)

    @Test
    fun `french system calculates fixed installment`() {
        val schedule =
            AmortizationCalculator.calculate(
                criteria(1000, 12, 12, AmortizationSystem.FRENCH),
            )

        assertEquals(12, schedule.items.size)
        // C = 1000 * 0.01 * 1.01^12 / (1.01^12 - 1) = 88.85
        assertEquals(BigDecimal("88.85"), schedule.items.first().installment)
        assertEquals(schedule.items.first().installment, schedule.items[10].installment)
        assertEquals(BigDecimal("10.00"), schedule.items.first().interest)
        assertEquals(BigDecimal("0.00"), schedule.items.last().balance)
        assertEquals(schedule.totalPayment, schedule.totalInterest.add(schedule.principal))
    }

    @Test
    fun `german system calculates decreasing installments`() {
        val schedule =
            AmortizationCalculator.calculate(
                criteria(1200, 12, 12, AmortizationSystem.GERMAN),
            )

        assertEquals(12, schedule.items.size)
        // Fixed principal 100/month; first installment 100 + 1200*1% = 112, last 100 + 100*1% = 101
        assertEquals(BigDecimal("112.00"), schedule.items.first().installment)
        assertEquals(BigDecimal("101.00"), schedule.items.last().installment)
        assertEquals(BigDecimal("100.00"), schedule.items.first().principal)
        assertEquals(BigDecimal("78.00"), schedule.totalInterest)
        assertEquals(BigDecimal("1278.00"), schedule.totalPayment)
        assertEquals(BigDecimal("0.00"), schedule.items.last().balance)
    }

    @Test
    fun `zero rate produces principal-only installments`() {
        val schedule =
            AmortizationCalculator.calculate(
                criteria(1000, 0, 10, AmortizationSystem.FRENCH),
            )

        assertEquals(BigDecimal("100.00"), schedule.items.first().installment)
        assertEquals(BigDecimal("0.00"), schedule.totalInterest)
        assertEquals(BigDecimal("1000.00"), schedule.totalPayment)
        assertEquals(BigDecimal("0.00"), schedule.items.last().balance)
    }

    @Test
    fun `due dates advance one month from start date`() {
        val schedule =
            AmortizationCalculator.calculate(
                criteria(100, 12, 3, AmortizationSystem.FRENCH),
            )

        assertEquals(startDate, schedule.items[0].dueDate)
        assertEquals(startDate.plusMonths(1), schedule.items[1].dueDate)
        assertEquals(startDate.plusMonths(2), schedule.items[2].dueDate)
    }

    @Test
    fun `last installment reconciles rounding remainder`() {
        val schedule =
            AmortizationCalculator.calculate(
                criteria(100, 33.33, 7, AmortizationSystem.GERMAN),
            )

        val principalSum = schedule.items.sumOf { it.principal }

        assertEquals(BigDecimal("0.00"), schedule.items.last().balance)
        assertEquals(0, principalSum.compareTo(BigDecimal("100.00")))
    }

    private fun criteria(
        principal: Int,
        annualRate: Number,
        installments: Int,
        system: AmortizationSystem,
    ) = ScheduleCriteria(
        principal = BigDecimal(principal),
        annualRate = BigDecimal(annualRate.toString()),
        installments = installments,
        system = system,
        startDate = startDate,
    )
}
