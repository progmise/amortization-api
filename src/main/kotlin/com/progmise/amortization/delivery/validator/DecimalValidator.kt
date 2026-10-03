package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.exception.ExceptionCode
import com.progmise.amortization.utils.generateDecimalException
import com.progmise.amortization.utils.validator.Validator

class DecimalValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (isValid(data).not()) {
            exceptions.add(generateDecimalException(fieldNames))
        }

        return exceptions
    }

    companion object {
        private val REGEX = """^\d+(\.\d{1,2})?$""".toRegex()

        fun isValid(data: String): Boolean = REGEX.containsMatchIn(data)
    }
}
