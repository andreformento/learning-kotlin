package com.andreformento.money.user.api

import com.andreformento.money.user.security.UserCredentials
import com.andreformento.money.user.security.api.UserSignupCreation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.util.LinkedMultiValueMap
import kotlin.random.Random


@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `can obtain own user details when logged in`() {
        // arrange
        // TODO this is very wrong!!!
        val email = "new@example.com" + Random.nextInt()

        // act
        webClient
            .post()
            .uri("/user/auth/signup")
            .bodyValue(UserSignupCreation(name = "user test", email = email, password = "pw"))
            .exchange()
            .expectStatus().isNoContent

        val loginResponse = webClient
            .post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials(email, "pw"))
            .exchange()
            .expectStatus().isOk
            .returnResult<Any>()

        // TODO why it isn't working?
        val xAuth = loginResponse
            .responseHeaders
            .getValuesAsList("Set-Cookie")[0]!!
            .replaceFirst("X-Auth=", "")
            .split(";")[0]

        println("LinkedMultiValueMap(mapOf-> " + LinkedMultiValueMap(mapOf("X-Auth" to listOf(xAuth))))
        webClient
            .get()
            .uri("/user")
            .cookies { it.addAll(LinkedMultiValueMap(mapOf("X-Auth" to listOf(xAuth)))) }
            .exchange()
            .expectStatus().isOk
            .expectBody<LoggedUserResponse>().isEqualTo(LoggedUserResponse(name = "user test", email = email))
    }

}
