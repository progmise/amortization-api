package com.progmise.amortization.delivery.validator

import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.generateTypeException
import com.progmise.utils.validator.Validator

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
