package com.progmise.amortization.utils.validator

import com.progmise.amortization.delivery.exception.ExceptionCode

class CompositeValidator<T>(
    private val validators: List<Validator<T>>,
) : Validator<T> {
    override fun validate(
        data: T,
        fieldNames: List<String>,
    ): List<ExceptionCode> = validators.flatMap { it.validate(data, fieldNames) }
}
