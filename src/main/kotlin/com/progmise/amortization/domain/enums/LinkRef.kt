package com.progmise.amortization.domain.enums

enum class LinkRef(
    val type: String,
) {
    FIRST("first"),
    PREVIOUS("previous"),
    NEXT("next"),
    LAST("last"),
}
