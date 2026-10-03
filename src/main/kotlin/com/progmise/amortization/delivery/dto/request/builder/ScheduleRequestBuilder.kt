package com.progmise.amortization.delivery.dto.request.builder

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.delivery.validator.ScheduleRequestValidator
import com.progmise.amortization.domain.entity.ScheduleCriteria
import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.utils.exception.BadRequestException
import com.progmise.utils.util.Constants.ERROR_BODY
import com.progmise.utils.util.Constants.ISO_DATE_PATTERN
import com.progmise.utils.util.toLocalDate
import java.math.BigDecimal

class ScheduleRequestBuilder(
    private val validator: ScheduleRequestValidator,
) {
    fun build(data: CreateScheduleRequest): ScheduleCriteria {
        val exceptions = validator.validate(data, ArrayList())

        if (exceptions.isNotEmpty()) {
            throw BadRequestException(exceptions = exceptions, errorCodeGeneral = ERROR_BODY)
        }

        return ScheduleCriteria(
            principal = BigDecimal(data.principal),
            annualRate = BigDecimal(data.annualRate),
            installments = data.installments!!.toInt(),
            system = AmortizationSystem.of(data.system!!)!!,
            startDate = data.startDate.toLocalDate(ISO_DATE_PATTERN)!!,
        )
    }
}
