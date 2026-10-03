package com.progmise.amortization.infrastructure.cache

import com.fasterxml.jackson.core.type.TypeReference

class NoOpCache : Cache {
    override fun <T> getObject(
        key: String,
        typeReference: TypeReference<T>,
    ): T? = null

    override fun fastPut(
        key: String,
        value: Any,
    ) = Unit

    override fun delete(key: String) = Unit
}
