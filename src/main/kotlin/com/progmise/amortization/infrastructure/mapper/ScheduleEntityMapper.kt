package com.progmise.amortization.infrastructure.mapper

import com.progmise.amortization.domain.entity.Installment
import com.progmise.amortization.domain.entity.Schedule
import com.progmise.amortization.domain.enums.AmortizationSystem
import com.progmise.amortization.infrastructure.persistence.entity.InstallmentEntity
import com.progmise.amortization.infrastructure.persistence.entity.ScheduleEntity
import org.springframework.stereotype.Component

@Component
class ScheduleEntityMapper {
    fun toDomain(entity: ScheduleEntity): Schedule =
        Schedule(
            id = entity.id,
            principal = entity.principal,
            annualRate = entity.annualRate,
            installments = entity.installments,
            system = AmortizationSystem.valueOf(entity.system),
            startDate = entity.startDate,
            items = entity.items.map { toDomain(it) },
            totalInterest = entity.totalInterest,
            totalPayment = entity.totalPayment,
        )

    fun toEntity(domain: Schedule): ScheduleEntity {
        val entity =
            ScheduleEntity(
                id = requireNotNull(domain.id) { "Schedule id is required for persistence" },
                principal = domain.principal,
                annualRate = domain.annualRate,
                installments = domain.installments,
                system = domain.system.name,
                startDate = domain.startDate,
                totalInterest = domain.totalInterest,
                totalPayment = domain.totalPayment,
            )

        entity.items.addAll(
            domain.items.map {
                InstallmentEntity(
                    schedule = entity,
                    number = it.number,
                    dueDate = it.dueDate,
                    installment = it.installment,
                    principal = it.principal,
                    interest = it.interest,
                    balance = it.balance,
                )
            },
        )

        return entity
    }

    private fun toDomain(entity: InstallmentEntity): Installment =
        Installment(
            number = entity.number,
            dueDate = entity.dueDate,
            installment = entity.installment,
            principal = entity.principal,
            interest = entity.interest,
            balance = entity.balance,
        )
}
