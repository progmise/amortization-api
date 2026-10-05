package com.progmise.amortization.application.controller

import com.progmise.amortization.application.controller.ScheduleResource.Companion.SCHEDULES_RESOURCE
import com.progmise.amortization.delivery.controller.ScheduleController
import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.delivery.dto.request.builder.ScheduleIdRequestBuilder
import com.progmise.amortization.delivery.dto.request.builder.ScheduleRequestBuilder
import com.progmise.amortization.delivery.dto.response.ScheduleDTO
import com.progmise.amortization.delivery.dto.response.ScheduleListDTO
import com.progmise.amortization.delivery.dto.response.ScheduleSummaryDTO
import com.progmise.amortization.delivery.validator.ScheduleIdValidator
import com.progmise.amortization.delivery.validator.ScheduleRequestValidator
import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.amortization.domain.enums.FeatureToggle
import com.progmise.amortization.domain.exception.ScheduleNotFoundException
import com.progmise.amortization.domain.repository.ScheduleRepository
import com.progmise.amortization.domain.service.AmortizationCalculator
import io.github.progmise.commons.delivery.ListPaginationDTO
import io.github.progmise.commons.delivery.dto.request.builder.PaginationRequestBuilder
import io.github.progmise.commons.exception.BadRequestException
import io.github.progmise.commons.infrastructure.FeatureToggleHelper
import io.github.progmise.commons.util.Constants.ERROR_BODY
import io.github.progmise.commons.util.ExceptionCodeGenerators
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(SCHEDULES_RESOURCE)
class ScheduleResource(
    private val scheduleRepository: ScheduleRepository,
    private val featureToggleHelper: FeatureToggleHelper,
) : ScheduleController {
    private val scheduleRequestBuilder = ScheduleRequestBuilder(validator = ScheduleRequestValidator())
    private val scheduleIdRequestBuilder = ScheduleIdRequestBuilder(validator = ScheduleIdValidator())
    private val paginationRequestBuilder = PaginationRequestBuilder()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun createSchedule(
        @RequestBody request: CreateScheduleRequest,
    ): ScheduleDTO {
        val criteria = scheduleRequestBuilder.build(request)

        if (criteria.system == AmortizationSystem.GERMAN && featureToggleHelper.isActive(FeatureToggle.GERMAN_AMORTIZATION_ON).not()) {
            throw BadRequestException(
                listOf(ExceptionCodeGenerators.generateFeatureDisabledException(FeatureToggle.GERMAN_AMORTIZATION_ON.name)),
                ERROR_BODY,
            )
        }

        return ScheduleDTO(scheduleRepository.save(AmortizationCalculator.calculate(criteria)))
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getSchedule(
        @PathVariable id: String,
    ): ScheduleDTO {
        val validatedId = scheduleIdRequestBuilder.build(id)

        return ScheduleDTO(scheduleRepository.findById(validatedId) ?: throw ScheduleNotFoundException(validatedId))
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getSchedules(
        @RequestParam allParams: Map<String, String>,
    ): EntityModel<ScheduleListDTO> {
        val (offset, limit) = paginationRequestBuilder.build(allParams)
        val (schedules, totalSize) = scheduleRepository.findAll(offset, limit)

        return ListPaginationDTO
            .of(
                ScheduleListDTO(schedules.map { ScheduleSummaryDTO(it) }),
                totalSize,
                offset,
                limit,
            ).toEntityModel(allParams, currentUrl())
    }

    private fun currentUrl(): String = "$SERVLET_CONTEXT_PATH/$SCHEDULES_RESOURCE"

    companion object {
        const val SCHEDULES_RESOURCE = "schedules"
        private const val SERVLET_CONTEXT_PATH = "/api/1.0"
    }
}
