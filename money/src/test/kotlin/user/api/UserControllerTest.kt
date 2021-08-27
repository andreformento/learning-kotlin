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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UserControllerTest {

    @LocalServerPort
    var serverPort: Int? = null

//    @Test
    fun `can obtain own user details when logged in`() {
        // arrange
        val webClient = WebClient.builder()
            .baseUrl("http://localhost:${serverPort}")
            .build()

        // act
        val signupResponse = webClient.put()
            .uri("/user/auth/signup")
            .bodyValue(UserSignupCreation(name = "user test", email = "new@example.com", password = "pw"))
            .exchange()
            .block() ?: throw RuntimeException("Should have gotten a signup response")
        println(signupResponse)

        val loginResponse = webClient.post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials("new@example.com", "pw"))
            .exchange()
            .block() ?: throw RuntimeException("Should have gotten a login response")
        val responseCookies = loginResponse.cookies()
            .map { it.key to it.value.map { cookie -> cookie.value } }
            .toMap()

        val response = webClient.get()
            .uri("/user")
            .cookies { it.addAll(LinkedMultiValueMap(responseCookies)) }
            .exchange()
            .block()

        // assert
        assertThat(response?.statusCode()).isEqualTo(HttpStatus.OK)
        assertThat(
            response?.bodyToFlux(LoggedUserResponse::class.java)?.blockFirst()
        ).isEqualTo(LoggedUserResponse(name = "user test", email = "new@example.com"))
    }

}
