package com.progmise.amortization.delivery.dto.request.builder

import com.progmise.amortization.delivery.validator.ScheduleIdValidator
import com.progmise.amortization.utils.Constants.ERROR_PATH
import com.progmise.amortization.utils.exception.BadRequestException

class ScheduleIdRequestBuilder(
    private val validator: ScheduleIdValidator,
) {
    fun build(data: String): String {
        val exceptions = validator.validate(data, ArrayList())

        if (exceptions.isEmpty()) {
            return data
        } else {
            throw BadRequestException(exceptions = exceptions, errorCodeGeneral = ERROR_PATH)
        }
    }
}
