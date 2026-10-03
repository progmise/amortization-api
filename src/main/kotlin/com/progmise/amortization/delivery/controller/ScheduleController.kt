package com.progmise.amortization.delivery.controller

import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.delivery.dto.response.ScheduleDTO
import com.progmise.amortization.delivery.dto.response.ScheduleListDTO
import org.springframework.hateoas.EntityModel

interface ScheduleController {
    fun createSchedule(request: CreateScheduleRequest): ScheduleDTO

    fun getSchedule(id: String): ScheduleDTO

    fun getSchedules(allParams: Map<String, String>): EntityModel<ScheduleListDTO>
}
