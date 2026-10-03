package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.exception.ExceptionCode
import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.amortization.utils.generateTypeException
import com.progmise.amortization.utils.validator.Validator

class TypeValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (AmortizationSystem.of(data) == null) {
            exceptions.add(generateTypeException(fieldNames))
        }

        return exceptions
    }
}
