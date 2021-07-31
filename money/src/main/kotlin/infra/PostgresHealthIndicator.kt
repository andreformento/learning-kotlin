package com.andreformento.money.infra

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.repository.Organizations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class PostgresHealthIndicator(private val repository: Organizations) : HealthIndicator {
    // TODO this is the best way? I hope not!
    // https://www.baeldung.com/spring-boot-health-indicators
    // https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/ReactiveHealthIndicator.html
    override fun health(): Health {
        return try {
            var firstEntity: Organization?
            runBlocking(Dispatchers.Default) {
                firstEntity = repository.findAll().firstOrNull()
            }

            if (firstEntity == null) {
                Health.down().withDetail("Cannot get any data from database", object {}).build()
            } else {
                println("firstPost $firstEntity")
                Health.up().build()
            }
        } catch (e: Exception) {
            Health.down().withDetail("Cannot connect to database", e).build()
        }
    }
}
