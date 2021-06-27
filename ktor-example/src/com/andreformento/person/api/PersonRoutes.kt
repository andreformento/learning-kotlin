package com.andreformento.person.api

import com.andreformento.person.Person
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

// https://ktor.io/docs/features-locations.html#subroutes
// https://github.com/Kotlin/kotlin-fullstack-sample/blob/master/backend/src/org/jetbrains/demo/thinkter/Locations.kt
// https://ktor.io/docs/requests.html#route_parameters

@Serializable
data class PersonData(val fullName: String)

fun Application.v1PeopleApi() {
    routing {
        route("/v1/people") {
            get {
                call.respond(setOf(Person("12ab", "André Formento")))
            }
            get("/{id}") {
                val id = call.parameters["id"].toString()
                call.respond(Person(id, "André Formento"))
            }
            post {
                val creationData = call.receive<PersonData>()
                call.respond(Person("aa", creationData.fullName))
            }
            put("/{id}") {
                val id = call.parameters["id"].toString()
                val creationData = call.receive<PersonData>()
                call.respond(Person(id, creationData.fullName))
            }
        }
    }
}
