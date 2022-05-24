package com.andreformento.money.user.api

import com.andreformento.money.user.UserId
import com.andreformento.money.user.security.UserCredentials
import com.andreformento.money.user.security.api.CreatedUser
import com.andreformento.money.user.security.api.UserSignupCreation
import com.andreformento.money.user.toUserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.util.LinkedMultiValueMap
import kotlin.random.Random

class CreatedUserForTest(
    val id:UserId,
    val email:String,
    val password:String,
)

fun WebTestClient.createUser(): CreatedUserForTest {
    // TODO this is very wrong!!!
    val email = "eddie${Random.nextInt()}@test.com"
    val password = "pass"

    val createdUser = this
        .post()
        .uri("/user/auth/signup")
        .bodyValue(UserSignupCreation(name = "Eddie", email = email, password = password))
        .exchange()
        .expectStatus().isCreated
        .returnResult<CreatedUser>().responseBody.blockFirst()!!

    assertThat(createdUser.name).isEqualTo("Eddie")
    assertThat(createdUser.email).isEqualTo(email)
    assertThat(createdUser.id).isNotEmpty()

    return CreatedUserForTest(
        id = createdUser.id.toUserId(),
        email = createdUser.email,
        password = password,
    )
}

@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `can obtain own user details when logged in after create`() {
        val createdUser = webClient.createUser()

        val loginResponse = webClient
            .post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials(createdUser.email, createdUser.password))
            .exchange()
            .expectStatus().isOk
            .returnResult<Any>()

        // TODO why it isn't working?
        val xAuth = loginResponse
            .responseHeaders
            .getValuesAsList("Set-Cookie")[0]!!
            .replaceFirst("X-Auth=", "")
            .split(";")[0]

        val xAuthCookie = LinkedMultiValueMap(mapOf("X-Auth" to listOf(xAuth)))

        webClient
            .get()
            .uri("/user")
            .cookies { it.addAll(xAuthCookie) }
            .exchange()
            .expectStatus().isOk
            .expectBody<LoggedUserResponse>().
            isEqualTo(LoggedUserResponse(name = "Eddie", email = createdUser.email))
    }

    @Test
    fun `can't login when email doesn't exists`() {
        val email = "no-user@example.com"
        val password = "nopass"

        webClient
            .post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials(email, password))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `can't login when password is wrong`() {
        val createdUser = webClient.createUser()

        val email = createdUser.email
        val password = "WRONG_${createdUser.password}"

        webClient
            .post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials(email, password))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `can not obtain own user details when logged off`() {
        webClient
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().isUnauthorized
    }

}
