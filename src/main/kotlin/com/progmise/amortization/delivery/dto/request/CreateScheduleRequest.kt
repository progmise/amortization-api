package com.progmise.amortization.delivery.dto.request

data class CreateScheduleRequest(
    val principal: String? = null,
    val annualRate: String? = null,
    val installments: String? = null,
    val system: String? = null,
    val startDate: String? = null,
)
