package com.progmise.amortization.application.controller

import com.progmise.amortization.application.controller.ScheduleResource.Companion.SCHEDULES_RESOURCE
import com.progmise.amortization.delivery.controller.ScheduleController
import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.delivery.dto.request.builder.PaginationRequestBuilder
import com.progmise.amortization.delivery.dto.request.builder.ScheduleIdRequestBuilder
import com.progmise.amortization.delivery.dto.request.builder.ScheduleRequestBuilder
import com.progmise.amortization.delivery.dto.response.ListPaginationDTO
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
import com.progmise.amortization.infrastructure.togglz.FeatureToggleHelper
import com.progmise.amortization.utils.Constants.ERROR_BODY
import com.progmise.amortization.utils.exception.BadRequestException
import com.progmise.amortization.utils.generateFeatureDisabledException
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
                exceptions = listOf(generateFeatureDisabledException(FeatureToggle.GERMAN_AMORTIZATION_ON.name)),
                errorCodeGeneral = ERROR_BODY,
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
                list = ScheduleListDTO(schedules.map { ScheduleSummaryDTO(it) }),
                totalSize = totalSize,
                offset = offset,
                limit = limit,
            ).toEntityModel(allParams, currentUrl())
    }

    private fun currentUrl(): String = "$SERVLET_CONTEXT_PATH/$SCHEDULES_RESOURCE"

    companion object {
        const val SCHEDULES_RESOURCE = "schedules"
        private const val SERVLET_CONTEXT_PATH = "/api/1.0"
    }
}
