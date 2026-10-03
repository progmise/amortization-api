package com.progmise.amortization.infrastructure.togglz

import com.progmise.amortization.domain.enums.FeatureToggle
import com.progmise.amortization.utils.logger
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Service
import org.togglz.core.manager.FeatureManager

@Service
class FeatureToggleHelper(
    private val featureManager: FeatureManager,
) {
    private val log = logger()

    fun isActive(feature: FeatureToggle): Boolean =
        try {
            val result = featureManager.isActive { feature.name }

            Metrics
                .counter("featureToggle", "featureName", feature.name, "status", if (result) "Active" else "NotActive")
                .increment()

            result
        } catch (e: Exception) {
            log.error("Unable to acquire the state for feature toggle {}: {}", feature.name, e.message)
            false
        }
}
