package com.progmise.amortization.delivery.exception

import com.progmise.amortization.domain.exception.ScheduleNotFoundException
import com.progmise.amortization.utils.generateScheduleNotFoundException
import com.progmise.utils.dto.ApiError
import com.progmise.utils.dto.ErrorsResponse
import com.progmise.utils.enums.ErrorLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DomainExceptionHandler {
    @ExceptionHandler(ScheduleNotFoundException::class)
    fun handleNotFound(exception: ScheduleNotFoundException): ResponseEntity<ErrorsResponse> {
        val code = generateScheduleNotFoundException("")

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = code.code,
                        message = exception.message ?: code.message,
                        level = ErrorLevel.WARNING,
                    ),
                ),
            ),
        )
    }
}
