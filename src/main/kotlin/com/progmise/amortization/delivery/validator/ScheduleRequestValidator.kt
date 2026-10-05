package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import io.github.progmise.commons.exception.ExceptionCode
import io.github.progmise.commons.util.ExceptionCodeGenerators
import io.github.progmise.commons.util.Extensions
import io.github.progmise.commons.validator.IntegerValidator
import io.github.progmise.commons.validator.MajorOrEqualValidator
import io.github.progmise.commons.validator.Validator

class ScheduleRequestValidator :
    BaseValidator(),
    Validator<CreateScheduleRequest> {
    private val minInstallmentsValidator = MajorOrEqualValidator(MIN_INSTALLMENTS)

    override fun validate(
        data: CreateScheduleRequest,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        exceptions.addAll(validateRequiredFields(data, fieldNames))

        Extensions.ifNotNullAndBlank(data.principal) {
            exceptions.addAll(
                decimalValidator.validate(
                    it,
                    fieldNames.plus(CreateScheduleRequest::principal.name),
                ),
            )
        }

        Extensions.ifNotNullAndBlank(data.annualRate) {
            exceptions.addAll(
                decimalValidator.validate(
                    it,
                    fieldNames.plus(CreateScheduleRequest::annualRate.name),
                ),
            )
        }

        Extensions.ifNotNullAndBlank(data.installments) {
            val fields = fieldNames.plus(CreateScheduleRequest::installments.name)

            exceptions.addAll(integerValidator.validate(it, fields))

            if (IntegerValidator.isValid(it)) {
                exceptions.addAll(minInstallmentsValidator.validate(it, fields))
            }
        }

        Extensions.ifNotNullAndBlank(data.system) {
            exceptions.addAll(
                typeValidator.validate(
                    it,
                    fieldNames.plus(CreateScheduleRequest::system.name),
                ),
            )
        }

        Extensions.ifNotNullAndBlank(data.startDate) {
            exceptions.addAll(
                dateValidator.validate(
                    it,
                    fieldNames.plus(CreateScheduleRequest::startDate.name),
                ),
            )
        }

        return exceptions
    }

    private fun validateRequiredFields(
        data: CreateScheduleRequest,
        fieldNames: List<String>,
    ): List<ExceptionCode> {
        val exceptions = ArrayList<ExceptionCode>()

        listOf(
            data.principal to CreateScheduleRequest::principal.name,
            data.annualRate to CreateScheduleRequest::annualRate.name,
            data.installments to CreateScheduleRequest::installments.name,
            data.system to CreateScheduleRequest::system.name,
            data.startDate to CreateScheduleRequest::startDate.name,
        ).forEach { (value, field) ->
            if (value.isNullOrBlank()) {
                exceptions.add(ExceptionCodeGenerators.generateRequiredFieldException(fieldNames.plus(field)))
            }
        }

        return exceptions
    }

    companion object {
        private const val MIN_INSTALLMENTS = 1
    }
}
