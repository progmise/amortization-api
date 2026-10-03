package com.progmise.amortization.infrastructure.cache

import com.fasterxml.jackson.core.type.TypeReference

interface Cache {
    fun <T> getObject(
        key: String,
        typeReference: TypeReference<T>,
    ): T?

    fun fastPut(
        key: String,
        value: Any,
    )

    fun delete(key: String)
}
