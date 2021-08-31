package com.andreformento.money.user.api

import com.andreformento.money.user.security.UserCredentials
import com.andreformento.money.user.security.api.UserSignupCreation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UserControllerTest {

    @LocalServerPort
    var serverPort: Int? = null

    @Test
    fun `can obtain own user details when logged in`() {
        // arrange
        // TODO this is very wrong!!!
        val email = "new@example.com" + Random.nextInt()

        val webClient = WebClient
            .builder()
            .baseUrl("http://localhost:${serverPort}")
            .build()

        // act
        val signupResponse = webClient
            .post()
            .uri("/user/auth/signup")
            .bodyValue(UserSignupCreation(name = "user test", email = email, password = "pw"))
            .exchange()
            .block() ?: throw RuntimeException("Should have gotten a signup response")

        assertThat(signupResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT)

        val loginResponse = webClient
            .post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials(email, "pw"))
            .exchange()
            .block() ?: throw RuntimeException("Should have gotten a login response")
        val responseCookies = loginResponse.cookies()
            .map { it.key to it.value.map { cookie -> cookie.value } }
            .toMap()

        val response = webClient
            .get()
            .uri("/user")
            .cookies { it.addAll(LinkedMultiValueMap(responseCookies)) }
            .exchange()
            .block()

        // assert
        assertThat(response?.statusCode())
            .isEqualTo(HttpStatus.OK)

        assertThat(response?.bodyToMono(LoggedUserResponse::class.java)?.block())
            .isEqualTo(LoggedUserResponse(name = "user test", email = email))
    }

}
