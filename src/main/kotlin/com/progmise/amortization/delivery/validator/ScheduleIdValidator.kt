package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.exception.ExceptionCode
import com.progmise.amortization.utils.validator.Validator

class ScheduleIdValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (isValid(data).not()) {
            exceptions.add(
                ExceptionCode(
                    code = "invalid.value.${fieldNames.plus("id").joinToString(".")}",
                    message = "The id provided is not a valid schedule id",
                ),
            )
        }

        return exceptions
    }

    companion object {
        private val REGEX = """^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$""".toRegex()

        fun isValid(data: String): Boolean = REGEX.containsMatchIn(data)
    }
}
