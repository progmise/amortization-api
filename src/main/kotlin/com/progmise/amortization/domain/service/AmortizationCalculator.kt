package com.progmise.amortization.domain.service

import com.progmise.amortization.domain.entity.Installment
import com.progmise.amortization.domain.entity.Schedule
import com.progmise.amortization.domain.entity.ScheduleCriteria
import com.progmise.amortization.domain.enums.AmortizationSystem
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object AmortizationCalculator {
    private val MATH_CONTEXT = MathContext(20, RoundingMode.HALF_EVEN)
    private const val SCALE = 2
    private val MONTHS_PER_YEAR = BigDecimal("12")
    private val ONE_HUNDRED = BigDecimal("100")

    fun calculate(
        criteria: ScheduleCriteria,
        id: String? = null,
    ): Schedule {
        val monthlyRate = criteria.annualRate.divide(ONE_HUNDRED, MATH_CONTEXT).divide(MONTHS_PER_YEAR, MATH_CONTEXT)
        val items =
            when (criteria.system) {
                AmortizationSystem.FRENCH -> frenchItems(criteria, monthlyRate)
                AmortizationSystem.GERMAN -> germanItems(criteria, monthlyRate)
            }

        return Schedule(
            id = id,
            principal = criteria.principal.money(),
            annualRate = criteria.annualRate.money(),
            installments = criteria.installments,
            system = criteria.system,
            startDate = criteria.startDate,
            items = items,
            totalInterest = items.sumOf { it.interest }.money(),
            totalPayment = items.sumOf { it.installment }.money(),
        )
    }

    private fun frenchItems(
        criteria: ScheduleCriteria,
        monthlyRate: BigDecimal,
    ): List<Installment> {
        val n = criteria.installments
        val payment =
            if (monthlyRate.signum() == 0) {
                criteria.principal.divide(BigDecimal(n), MATH_CONTEXT)
            } else {
                val factor = monthlyRate.add(BigDecimal.ONE).pow(n, MATH_CONTEXT)
                criteria.principal
                    .multiply(monthlyRate, MATH_CONTEXT)
                    .multiply(factor, MATH_CONTEXT)
                    .divide(factor.subtract(BigDecimal.ONE), MATH_CONTEXT)
            }

        return buildItems(criteria, monthlyRate) { _, _ -> payment }
    }

    private fun germanItems(
        criteria: ScheduleCriteria,
        monthlyRate: BigDecimal,
    ): List<Installment> {
        val amortization = criteria.principal.divide(BigDecimal(criteria.installments), MATH_CONTEXT)

        return buildItems(criteria, monthlyRate) { _, balance ->
            amortization.add(balance.multiply(monthlyRate, MATH_CONTEXT), MATH_CONTEXT)
        }
    }

    private fun buildItems(
        criteria: ScheduleCriteria,
        monthlyRate: BigDecimal,
        paymentAt: (Int, BigDecimal) -> BigDecimal,
    ): List<Installment> {
        val items = ArrayList<Installment>(criteria.installments)
        var balance = criteria.principal

        for (number in 1..criteria.installments) {
            val isLast = number == criteria.installments
            val interest = balance.multiply(monthlyRate, MATH_CONTEXT).money()
            var installment = paymentAt(number, balance).money()
            var principal = installment.subtract(interest)

            if (isLast || principal >= balance) {
                principal = balance
                installment = principal.add(interest).money()
            }

            balance = balance.subtract(principal)

            items.add(
                Installment(
                    number = number,
                    dueDate = criteria.startDate.plusMonths(number.toLong() - 1),
                    installment = installment,
                    principal = principal.money(),
                    interest = interest,
                    balance = balance.money(),
                ),
            )
        }

        return items
    }

    private fun BigDecimal.money(): BigDecimal = setScale(SCALE, RoundingMode.HALF_EVEN)
}
