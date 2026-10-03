package com.progmise.amortization.utils

import com.progmise.utils.exception.ExceptionCode
import java.text.MessageFormat.format

private const val SCHEDULE_NOT_FOUND_CODE = "schedule.not.found"
private const val SCHEDULE_NOT_FOUND_MESSAGE_TEMPLATE = "Schedule with id ''{0}'' not found"

fun generateScheduleNotFoundException(id: String) =
    ExceptionCode(
        code = SCHEDULE_NOT_FOUND_CODE,
        message = format(SCHEDULE_NOT_FOUND_MESSAGE_TEMPLATE, id),
    )
