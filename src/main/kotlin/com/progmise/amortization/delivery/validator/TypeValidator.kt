package com.progmise.amortization.delivery.validator

import com.progmise.amortization.domain.enums.AmortizationSystem
import io.github.progmise.utils.exception.ExceptionCode
import io.github.progmise.utils.util.ExceptionCodeGenerators
import io.github.progmise.utils.validator.Validator

class TypeValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (AmortizationSystem.of(data) == null) {
            exceptions.add(ExceptionCodeGenerators.generateTypeException(fieldNames))
        }

        return exceptions
    }
}
