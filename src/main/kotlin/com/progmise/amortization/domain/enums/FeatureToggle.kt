package com.progmise.amortization.domain.enums

import org.togglz.core.annotation.EnabledByDefault
import org.togglz.core.context.FeatureContext

enum class FeatureToggle {
    @EnabledByDefault
    SCHEDULE_CACHE_ON,

    @EnabledByDefault
    GERMAN_AMORTIZATION_ON,
    ;

    fun isActive(): Boolean = FeatureContext.getFeatureManager().isActive { name }
}
