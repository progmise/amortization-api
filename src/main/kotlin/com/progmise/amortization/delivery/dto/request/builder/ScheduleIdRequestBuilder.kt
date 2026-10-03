package com.progmise.amortization.delivery.dto.request.builder

import com.progmise.amortization.delivery.validator.ScheduleIdValidator
import com.progmise.utils.exception.BadRequestException
import com.progmise.utils.util.Constants.ERROR_PATH

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
