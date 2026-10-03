package com.progmise.amortization.delivery.exception

import com.progmise.amortization.domain.enums.ErrorLevel

data class ApiError(
    val code: String,
    val message: String,
    val level: ErrorLevel,
    val description: String? = null,
)

data class ErrorsResponse(
    val errors: List<ApiError>,
)
