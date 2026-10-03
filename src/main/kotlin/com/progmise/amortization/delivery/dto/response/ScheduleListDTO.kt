package com.progmise.amortization.delivery.dto.response

import com.progmise.amortization.domain.entity.Schedule
import java.math.BigDecimal
import java.time.LocalDate

data class ScheduleSummaryDTO(
    val id: String?,
    val principal: BigDecimal,
    val annualRate: BigDecimal,
    val installments: Int,
    val system: String,
    val startDate: LocalDate,
    val totalInterest: BigDecimal,
    val totalPayment: BigDecimal,
) {
    constructor(schedule: Schedule) : this(
        id = schedule.id,
        principal = schedule.principal,
        annualRate = schedule.annualRate,
        installments = schedule.installments,
        system = schedule.system.name,
        startDate = schedule.startDate,
        totalInterest = schedule.totalInterest,
        totalPayment = schedule.totalPayment,
    )
}

data class ScheduleListDTO(
    val schedules: List<ScheduleSummaryDTO>,
)
