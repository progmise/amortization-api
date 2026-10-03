package com.progmise.amortization.domain.enums

enum class AmortizationSystem {
    FRENCH,
    GERMAN,
    ;

    companion object {
        fun of(value: String): AmortizationSystem? = entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}
