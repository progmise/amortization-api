package com.progmise.amortization.delivery.validator

import io.github.progmise.commons.validator.DateValidator
import io.github.progmise.commons.validator.DecimalValidator
import io.github.progmise.commons.validator.IntegerValidator
import io.github.progmise.commons.validator.NumericValidator

abstract class BaseValidator {
    protected val integerValidator = IntegerValidator()
    protected val numericValidator = NumericValidator()
    protected val decimalValidator = DecimalValidator()
    protected val dateValidator = DateValidator()
    protected val typeValidator = TypeValidator()
}
