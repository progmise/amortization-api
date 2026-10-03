package com.progmise.amortization.domain.entity

import java.math.BigDecimal
import java.time.LocalDate

data class Installment(
    val number: Int,
    val dueDate: LocalDate,
    val installment: BigDecimal,
    val principal: BigDecimal,
    val interest: BigDecimal,
    val balance: BigDecimal,
)
