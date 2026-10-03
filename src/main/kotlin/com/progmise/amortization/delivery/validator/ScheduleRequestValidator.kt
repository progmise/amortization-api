package com.progmise.amortization.delivery.validator

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.utils.exception.ExceptionCode
import com.progmise.utils.util.generateRequiredFieldException
import com.progmise.utils.util.ifNotNullAndBlank
import com.progmise.utils.validator.IntegerValidator
import com.progmise.utils.validator.MajorOrEqualValidator
import com.progmise.utils.validator.Validator

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

        data.principal.ifNotNullAndBlank {
            exceptions.addAll(
                decimalValidator.validate(
                    data = it,
                    fieldNames = fieldNames.plus(CreateScheduleRequest::principal.name),
                ),
            )
        }

        data.annualRate.ifNotNullAndBlank {
            exceptions.addAll(
                decimalValidator.validate(
                    data = it,
                    fieldNames = fieldNames.plus(CreateScheduleRequest::annualRate.name),
                ),
            )
        }

        data.installments.ifNotNullAndBlank {
            val fields = fieldNames.plus(CreateScheduleRequest::installments.name)

            exceptions.addAll(integerValidator.validate(data = it, fieldNames = fields))

            if (IntegerValidator.isValid(it)) {
                exceptions.addAll(minInstallmentsValidator.validate(data = it, fieldNames = fields))
            }
        }

        data.system.ifNotNullAndBlank {
            exceptions.addAll(
                typeValidator.validate(
                    data = it,
                    fieldNames = fieldNames.plus(CreateScheduleRequest::system.name),
                ),
            )
        }

        data.startDate.ifNotNullAndBlank {
            exceptions.addAll(
                dateValidator.validate(
                    data = it,
                    fieldNames = fieldNames.plus(CreateScheduleRequest::startDate.name),
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

        if (data.principal.isNullOrBlank()) {
            exceptions.add(generateRequiredFieldException(fieldNames.plus(CreateScheduleRequest::principal.name)))
        }

        if (data.annualRate.isNullOrBlank()) {
            exceptions.add(generateRequiredFieldException(fieldNames.plus(CreateScheduleRequest::annualRate.name)))
        }

        if (data.installments.isNullOrBlank()) {
            exceptions.add(generateRequiredFieldException(fieldNames.plus(CreateScheduleRequest::installments.name)))
        }

        if (data.system.isNullOrBlank()) {
            exceptions.add(generateRequiredFieldException(fieldNames.plus(CreateScheduleRequest::system.name)))
        }

        if (data.startDate.isNullOrBlank()) {
            exceptions.add(generateRequiredFieldException(fieldNames.plus(CreateScheduleRequest::startDate.name)))
        }

        return exceptions
    }

    companion object {
        private const val MIN_INSTALLMENTS = 1
    }
}
