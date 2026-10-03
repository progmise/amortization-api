package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.exception.ExceptionCode
import com.progmise.amortization.utils.Constants.ISO_DATE_PATTERN
import com.progmise.amortization.utils.generateDateException
import com.progmise.amortization.utils.toLocalDate
import com.progmise.amortization.utils.validator.Validator

class DateValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (data.toLocalDate(ISO_DATE_PATTERN) == null) {
            exceptions.add(generateDateException(fieldNames))
        }

        return exceptions
    }
}
