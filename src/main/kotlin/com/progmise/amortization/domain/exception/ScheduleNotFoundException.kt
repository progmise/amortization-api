package com.progmise.amortization.domain.exception

class ScheduleNotFoundException(
    id: String,
) : RuntimeException("Schedule with id '$id' not found")
