package com.progmise.amortization.delivery.validator

abstract class BaseValidator {
    protected val integerValidator = IntegerValidator()
    protected val numericValidator = NumericValidator()
    protected val decimalValidator = DecimalValidator()
    protected val dateValidator = DateValidator()
    protected val typeValidator = TypeValidator()
}
