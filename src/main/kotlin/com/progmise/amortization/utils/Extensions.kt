package com.progmise.amortization.utils

import com.fasterxml.jackson.core.type.TypeReference
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

inline fun <T, R> T?.ifNotNullAndBlank(callback: (T) -> R): R? where T : String? = if (isNotNullAndBlank()) this?.let(callback) else null

fun String?.isNotNullAndBlank(): Boolean = this.isNullOrBlank().not()

fun String?.toLocalDate(pattern: String): LocalDate? =
    try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        null
    }

inline fun <reified T> typeRef(): TypeReference<T> = object : TypeReference<T>() {}

inline fun <reified T : Any> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)
