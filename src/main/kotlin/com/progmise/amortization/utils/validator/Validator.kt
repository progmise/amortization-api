package com.progmise.amortization.utils.validator

import com.progmise.amortization.delivery.exception.ExceptionCode

interface Validator<T> {
    fun validate(
        data: T,
        fieldNames: List<String>,
    ): List<ExceptionCode>
}
