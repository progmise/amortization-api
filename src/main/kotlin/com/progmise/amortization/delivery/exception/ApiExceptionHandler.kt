package com.progmise.amortization.delivery.exception

import com.progmise.amortization.domain.enums.ErrorLevel
import com.progmise.amortization.domain.exception.ScheduleNotFoundException
import com.progmise.amortization.utils.exception.BadRequestException
import com.progmise.amortization.utils.exception.RequestException
import com.progmise.amortization.utils.generateScheduleNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<ErrorsResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorsResponse(
                exception.exceptions.map {
                    ApiError(
                        code = it.code,
                        message = it.message,
                        level = exception.level,
                        description = exception.errorCodeGeneral,
                    )
                },
            ),
        )

    @ExceptionHandler(RequestException::class)
    fun handleRequestException(exception: RequestException): ResponseEntity<ErrorsResponse> =
        ResponseEntity.status(exception.httpStatus).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = exception.errorCode,
                        message = exception.message ?: exception.errorCode,
                        level = exception.level,
                        description = exception.description,
                    ),
                ),
            ),
        )

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

    @ExceptionHandler(
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class,
    )
    fun handleMalformedRequest(exception: Exception): ResponseEntity<ErrorsResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = "invalid.request",
                        message = exception.message ?: "Malformed request",
                        level = ErrorLevel.ERROR,
                    ),
                ),
            ),
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(exception: Exception): ResponseEntity<ErrorsResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorsResponse(
                listOf(
                    ApiError(
                        code = "internal.error",
                        message = "An unexpected error occurred",
                        level = ErrorLevel.CRITICAL,
                    ),
                ),
            ),
        )
}
