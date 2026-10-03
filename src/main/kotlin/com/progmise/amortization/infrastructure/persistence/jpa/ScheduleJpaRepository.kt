package com.progmise.amortization.infrastructure.persistence.jpa

import com.progmise.amortization.infrastructure.persistence.entity.ScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScheduleJpaRepository : JpaRepository<ScheduleEntity, String>
