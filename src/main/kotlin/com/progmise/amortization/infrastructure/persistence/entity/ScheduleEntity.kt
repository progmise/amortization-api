package com.progmise.amortization.infrastructure.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "schedule")
class ScheduleEntity(
    @Id
    @Column(length = 36)
    val id: String,
    @Column(nullable = false, precision = 19, scale = 2)
    val principal: BigDecimal,
    @Column(name = "annual_rate", nullable = false, precision = 10, scale = 2)
    val annualRate: BigDecimal,
    @Column(nullable = false)
    val installments: Int,
    @Column(name = "amortization_system", nullable = false, length = 10)
    val system: String,
    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,
    @Column(name = "total_interest", nullable = false, precision = 19, scale = 2)
    val totalInterest: BigDecimal,
    @Column(name = "total_payment", nullable = false, precision = 19, scale = 2)
    val totalPayment: BigDecimal,
    @OneToMany(mappedBy = "schedule", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("number")
    val items: MutableList<InstallmentEntity> = mutableListOf(),
)
