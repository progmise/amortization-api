package com.progmise.amortization.application.config

import com.progmise.amortization.domain.enums.FeatureToggle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.togglz.core.activation.ActivationStrategyProvider
import org.togglz.core.activation.GradualActivationStrategy
import org.togglz.core.manager.FeatureManager
import org.togglz.core.manager.FeatureManagerBuilder
import org.togglz.core.repository.StateRepository
import org.togglz.core.repository.jdbc.JDBCStateRepository
import org.togglz.core.repository.util.DefaultMapSerializer
import org.togglz.core.spi.FeatureProvider
import org.togglz.core.user.UserProvider
import org.togglz.kotlin.EnumClassFeatureProvider
import javax.sql.DataSource

@Configuration
class TogglzConfiguration {
    @Bean
    fun featureManager(
        stateRepository: StateRepository,
        featureProvider: FeatureProvider,
        userProvider: UserProvider,
        gradualActivationStrategyProvider: ActivationStrategyProvider,
    ): FeatureManager =
        FeatureManagerBuilder()
            .stateRepository(stateRepository)
            .featureProvider(featureProvider)
            .userProvider(userProvider)
            .activationStrategyProvider(gradualActivationStrategyProvider)
            .build()

    @Bean
    fun stateRepository(dataSource: DataSource): StateRepository =
        JDBCStateRepository
            .newBuilder(dataSource)
            .tableName("FEATURE_TOGGLE")
            .createTable(true)
            .serializer(DefaultMapSerializer.singleline())
            .noCommit(true)
            .build()

    @Bean
    fun featureProvider(): FeatureProvider = EnumClassFeatureProvider(FeatureToggle::class.java)

    @Bean
    fun userProvider(): UserProvider = UserProvider { null }

    @Bean
    fun gradualActivationStrategyProvider(): ActivationStrategyProvider =
        ActivationStrategyProvider {
            listOf(GradualActivationStrategy())
        }
}
