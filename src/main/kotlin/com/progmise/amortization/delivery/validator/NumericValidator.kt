package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.exception.ExceptionCode
import com.progmise.amortization.utils.generateNumericException
import com.progmise.amortization.utils.validator.Validator

class NumericValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (isValid(data).not()) {
            exceptions.add(generateNumericException(fieldNames))
        }

        return exceptions
    }

    companion object {
        private val REGEX = """^\d+$""".toRegex()

        fun isValid(data: String): Boolean = REGEX.containsMatchIn(data)
    }
}
