package com.progmise.amortization.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "installment")
class InstallmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule: ScheduleEntity? = null,
    @Column(nullable = false)
    val number: Int,
    @Column(name = "due_date", nullable = false)
    val dueDate: LocalDate,
    @Column(nullable = false, precision = 19, scale = 2)
    val installment: BigDecimal,
    @Column(nullable = false, precision = 19, scale = 2)
    val principal: BigDecimal,
    @Column(nullable = false, precision = 19, scale = 2)
    val interest: BigDecimal,
    @Column(nullable = false, precision = 19, scale = 2)
    val balance: BigDecimal,
)
