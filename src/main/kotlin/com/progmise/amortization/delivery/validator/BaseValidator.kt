package com.progmise.amortization.delivery.validator

import com.progmise.utils.validator.DateValidator
import com.progmise.utils.validator.DecimalValidator
import com.progmise.utils.validator.IntegerValidator
import com.progmise.utils.validator.NumericValidator

abstract class BaseValidator {
    protected val integerValidator = IntegerValidator()
    protected val numericValidator = NumericValidator()
    protected val decimalValidator = DecimalValidator()
    protected val dateValidator = DateValidator()
    protected val typeValidator = TypeValidator()
}
