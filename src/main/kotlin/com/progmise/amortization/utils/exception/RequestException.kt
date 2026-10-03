package com.progmise.amortization.utils.exception

import com.progmise.amortization.domain.enums.ErrorLevel
import org.springframework.http.HttpStatus

open class RequestException(
    val httpStatus: HttpStatus,
    val errorCode: String,
    errorMessage: String,
    val level: ErrorLevel = ErrorLevel.ERROR,
    val description: String? = null,
) : RuntimeException(errorMessage)
