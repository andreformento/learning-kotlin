package com.andreformento

import com.andreformento.person.api.v1PeopleApi
import com.google.gson.FieldNamingPolicy
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
    install(ContentNegotiation) {
        gson {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        }
    }
    install(Routing)

    routing {
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
        v1PeopleApi()
    }
}
