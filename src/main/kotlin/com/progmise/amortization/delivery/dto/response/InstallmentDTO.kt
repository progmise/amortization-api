package com.progmise.amortization.delivery.dto.response

import com.progmise.amortization.domain.entity.Installment
import java.math.BigDecimal
import java.time.LocalDate

data class InstallmentDTO(
    val number: Int,
    val dueDate: LocalDate,
    val installment: BigDecimal,
    val principal: BigDecimal,
    val interest: BigDecimal,
    val balance: BigDecimal,
) {
    constructor(installment: Installment) : this(
        number = installment.number,
        dueDate = installment.dueDate,
        installment = installment.installment,
        principal = installment.principal,
        interest = installment.interest,
        balance = installment.balance,
    )
}
