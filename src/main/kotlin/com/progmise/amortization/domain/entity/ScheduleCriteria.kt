package com.progmise.amortization.domain.entity

import com.progmise.amortization.domain.enums.AmortizationSystem
import java.math.BigDecimal
import java.time.LocalDate

data class ScheduleCriteria(
    val principal: BigDecimal,
    val annualRate: BigDecimal,
    val installments: Int,
    val system: AmortizationSystem,
    val startDate: LocalDate,
)
