package com.andreformento

import io.ktor.http.*
import io.ktor.request.*
import io.ktor.server.testing.*
import net.javacrumbs.jsonunit.assertj.assertThatJson
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun shouldGetPeople() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/v1/people").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertThatJson(response.content.toString()) {
                    isArray
                    node("[0].id").isEqualTo("12ab")
                    node("[0].full_name").isEqualTo("André Formento")
                }
            }
        }
    }

    @Test
    fun shouldGetPersonById() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/v1/people/12ab").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertThatJson(response.content.toString()) {
                    isObject
                    node("id").isEqualTo("12ab")
                    node("full_name").isEqualTo("André Formento")
                }
            }
        }
    }

    @Test
    fun shouldCreatePerson() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/v1/people") {
                addHeader("Content-Type", "application/json")
                setBody(""" {"full_name":"blah"} """)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertThatJson(response.content.toString()) {
                    isObject
                    node("id").isString.isNotBlank
                    node("full_name").isEqualTo("blah")
                }
            }
        }
    }

    @Test
    fun shouldEditPerson() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Put, "/v1/people/12ab") {
                addHeader("Content-Type", "application/json")
                setBody(""" {"full_name":"blah"} """)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertThatJson(response.content.toString()) {
                    isObject
                    node("id").isEqualTo("12ab")
                    node("full_name").isEqualTo("blah")
                }
            }
        }
    }
}
