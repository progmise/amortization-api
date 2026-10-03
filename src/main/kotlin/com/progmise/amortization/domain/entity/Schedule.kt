package com.progmise.amortization.domain.entity

import com.progmise.amortization.domain.enums.AmortizationSystem
import java.math.BigDecimal
import java.time.LocalDate

data class Schedule(
    val id: String?,
    val principal: BigDecimal,
    val annualRate: BigDecimal,
    val installments: Int,
    val system: AmortizationSystem,
    val startDate: LocalDate,
    val items: List<Installment>,
    val totalInterest: BigDecimal,
    val totalPayment: BigDecimal,
)
