package com.progmise.amortization.delivery.validator

import io.github.progmise.utils.exception.ExceptionCode
import io.github.progmise.utils.validator.Validator

class ScheduleIdValidator : Validator<String> {
    override fun validate(
        data: String,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        if (isValid(data).not()) {
            exceptions.add(
                ExceptionCode(
                    "invalid.value.${fieldNames.plus("id").joinToString(".")}",
                    "The id provided is not a valid schedule id",
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
