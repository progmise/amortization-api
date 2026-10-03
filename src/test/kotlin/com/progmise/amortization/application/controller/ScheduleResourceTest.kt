package com.progmise.amortization.application.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.progmise.amortization.delivery.dto.request.CreateScheduleRequest
import com.progmise.amortization.domain.entity.ScheduleCriteria
import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.amortization.domain.enums.FeatureToggle
import com.progmise.amortization.domain.repository.ScheduleRepository
import com.progmise.amortization.domain.service.AmortizationCalculator
import com.progmise.amortization.infrastructure.togglz.FeatureToggleHelper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(ScheduleResource::class)
class ScheduleResourceTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var scheduleRepository: ScheduleRepository

    @MockitoBean
    private lateinit var featureToggleHelper: FeatureToggleHelper

    private val schedule =
        AmortizationCalculator.calculate(
            ScheduleCriteria(
                principal = BigDecimal("1200"),
                annualRate = BigDecimal("12"),
                installments = 12,
                system = AmortizationSystem.GERMAN,
                startDate = LocalDate.of(2026, 2, 1),
            ),
            id = SCHEDULE_ID,
        )

    @Test
    fun `POST creates a schedule and returns 201`() {
        whenever(featureToggleHelper.isActive(any())).thenReturn(true)
        whenever(scheduleRepository.save(any())).thenReturn(schedule)

        mockMvc
            .perform(
                post("/schedules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest(system = "GERMAN"))),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(SCHEDULE_ID))
            .andExpect(jsonPath("$.system").value("GERMAN"))
            .andExpect(jsonPath("$.totalInterest").value(78.0))
            .andExpect(jsonPath("$.items[0].installment").value(112.0))
            .andExpect(jsonPath("$.items[11].balance").value(0.0))
    }

    @Test
    fun `POST with missing fields returns 400 with the errors contract`() {
        mockMvc
            .perform(
                post("/schedules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(CreateScheduleRequest(principal = "100"))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors").isArray)
            .andExpect(jsonPath("$.errors.length()").value(4))
            .andExpect(jsonPath("$.errors[?(@.code=='required.field.annualRate')]").exists())
            .andExpect(jsonPath("$.errors[0].level").value("ERROR"))
    }

    @Test
    fun `POST german system returns 400 when the toggle is off`() {
        whenever(featureToggleHelper.isActive(FeatureToggle.GERMAN_AMORTIZATION_ON)).thenReturn(false)

        mockMvc
            .perform(
                post("/schedules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest(system = "GERMAN"))),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0].code").value("feature.disabled.german_amortization_on"))
    }

    @Test
    fun `GET by id returns the schedule`() {
        whenever(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(schedule)

        mockMvc
            .perform(get("/schedules/$SCHEDULE_ID"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(SCHEDULE_ID))
            .andExpect(jsonPath("$.items").isArray)
    }

    @Test
    fun `GET by id returns 404 when it does not exist`() {
        whenever(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(null)

        mockMvc
            .perform(get("/schedules/$SCHEDULE_ID"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errors[0].code").value("schedule.not.found"))
    }

    @Test
    fun `GET by id returns 400 for a malformed id`() {
        mockMvc
            .perform(get("/schedules/not-an-id"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0].code").value("invalid.value.id"))
    }

    @Test
    fun `GET list returns schedules with pagination links`() {
        whenever(scheduleRepository.findAll(0, 20)).thenReturn(listOf(schedule) to 1L)

        mockMvc
            .perform(get("/schedules"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.schedules[0].id").value(SCHEDULE_ID))
            .andExpect(jsonPath("$._links.first.href").exists())
    }

    @Test
    fun `GET list returns 400 for a non numeric offset`() {
        mockMvc
            .perform(get("/schedules?_offset=abc"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors[0].code").value("invalid.value._offset"))
    }

    private fun validRequest(system: String) =
        CreateScheduleRequest(
            principal = "1200",
            annualRate = "12",
            installments = "12",
            system = system,
            startDate = "2026-02-01",
        )

    companion object {
        private const val SCHEDULE_ID = "5f7b2f7e-9a6b-4f0e-a3c1-8d6e5f4a3b2c"
    }
}
