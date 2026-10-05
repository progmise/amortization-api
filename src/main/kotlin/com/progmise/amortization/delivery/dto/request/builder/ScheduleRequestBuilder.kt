package com.progmise.amortization.delivery.dto.request.builder

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.delivery.validator.ScheduleRequestValidator
import com.progmise.amortization.domain.entity.ScheduleCriteria
import com.progmise.amortization.domain.enums.AmortizationSystem
import io.github.progmise.commons.exception.BadRequestException
import io.github.progmise.commons.util.Constants.ERROR_BODY
import io.github.progmise.commons.util.Constants.ISO_DATE_PATTERN
import io.github.progmise.commons.util.Extensions
import java.math.BigDecimal

class ScheduleRequestBuilder(
    private val validator: ScheduleRequestValidator,
) {
    fun build(data: CreateScheduleRequest): ScheduleCriteria {
        val exceptions = validator.validate(data, ArrayList())

        if (exceptions.isNotEmpty()) {
            throw BadRequestException(exceptions, ERROR_BODY)
        }

        return ScheduleCriteria(
            principal = BigDecimal(data.principal),
            annualRate = BigDecimal(data.annualRate),
            installments = data.installments!!.toInt(),
            system = AmortizationSystem.of(data.system!!)!!,
            startDate = Extensions.toLocalDate(data.startDate, ISO_DATE_PATTERN)!!,
        )
    }
}
