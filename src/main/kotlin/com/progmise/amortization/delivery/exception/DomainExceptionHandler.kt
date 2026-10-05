package com.progmise.amortization.delivery.exception

import com.progmise.amortization.domain.exception.ScheduleNotFoundException
import com.progmise.amortization.utils.generateScheduleNotFoundException
import io.github.progmise.commons.dto.ApiError
import io.github.progmise.commons.dto.ErrorsResponse
import io.github.progmise.commons.enums.ErrorLevel
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
                        code.code(),
                        exception.message ?: code.message(),
                        ErrorLevel.WARNING,
                    ),
                ),
            ),
        )
    }
}
