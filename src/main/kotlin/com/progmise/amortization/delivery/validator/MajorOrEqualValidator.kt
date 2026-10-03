package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.exception.ExceptionCode
import com.progmise.amortization.utils.generateValueNotMajorOrEqualException
import com.progmise.amortization.utils.validator.Validator

class MajorOrEqualValidator(
    private val bound: Int,
) : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()
        val value = data.toIntOrNull()

        if (value == null || value < bound) {
            exceptions.add(generateValueNotMajorOrEqualException(fieldNames, bound))
        }

        return exceptions
    }
}
