package com.progmise.amortization.delivery.validator

import io.github.progmise.utils.validator.DateValidator
import io.github.progmise.utils.validator.DecimalValidator
import io.github.progmise.utils.validator.IntegerValidator
import io.github.progmise.utils.validator.NumericValidator

abstract class BaseValidator {
    protected val integerValidator = IntegerValidator()
    protected val numericValidator = NumericValidator()
    protected val decimalValidator = DecimalValidator()
    protected val dateValidator = DateValidator()
    protected val typeValidator = TypeValidator()
}
