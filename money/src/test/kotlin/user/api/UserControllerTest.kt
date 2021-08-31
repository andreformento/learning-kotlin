package com.andreformento.money.user.api

import com.andreformento.money.user.security.UserCredentials
import com.andreformento.money.user.security.api.UserSignupCreation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
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

//        client
//            .post("/user/auth/signup")
//            .andExpect {
//                status { isNoContent() }
//            }

        // act
        webClient
            .post()
            .uri("/user/auth/signup")
            .bodyValue(UserSignupCreation(name = "user test", email = email, password = "pw"))
            .exchange()
            .expectStatus().isNoContent
//            .block() ?: throw RuntimeException("Should have gotten a signup response")
//        assertThat(signupResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT)

        val loginResponse = webClient
            .post()
            .uri("/user/auth/login")
            .bodyValue(UserCredentials(email, "pw"))
            .exchange()
            .expectStatus().isOk
            .returnResult<Any>()

        println("Set-Cookie -> " + loginResponse.responseCookies)
        val xAuth = loginResponse.responseHeaders.getValuesAsList("Set-Cookie")[0]!!.replaceFirst("X-Auth", "").split(";")[0]
        println("value -> " + xAuth)

//        println("loginResponse.cookies() -> ${loginResponse.cookies()}")
//        println("responseCookies -> $responseCookies")
//        println("LinkedMultiValueMap(responseCookies) -> ${LinkedMultiValueMap(responseCookies)}")

//        loginResponse.cookies() -> {X-Auth=[X-Auth=eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJuZXdAZXhhbXBsZS5jb203NDk5NTA5NTYiLCJpc3MiOiJpZGVudGl0eSIsImV4cCI6MTYzMDQzODY4NSwiaWF0IjoxNjMwNDM3Nzg1fQ.JOgaS7g5pC5xX9CLASVdTeg6FHxfEC7QlKehpDAo0v1ayvxdbAPmD7F27sWsIlCRgX6lb3SMISB5CrVgrBCL4hJ1Z-WhEmba2Pi5NEUCJNnN0y6deattbYGlHL3l3WJx3MAxhjPHvDR-CP3ofr3Mmw1X9bNTWKWg0K46X9EcsYwCrfEMZ9_MaFtnVlDpuXY2XDovgbPzLZAwMLhy5YQrHYo0J8ah9nDP16t-y0QHx864vW8YQ4Wt7FJw_wBsRLmI3ynYjkWFBld9XgvFqImkskNrA9q99Hlji-6vxtFwM2xRUw6Z6Geyqc2GiYtSlpyHW_UvoGQNWR3vPsg4XM3XmQ; Path=/; Max-Age=3600; Expires=Tue, 31 Aug 2021 20:23:05 GMT; HttpOnly]}
//        responseCookies -> {X-Auth=[eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJuZXdAZXhhbXBsZS5jb203NDk5NTA5NTYiLCJpc3MiOiJpZGVudGl0eSIsImV4cCI6MTYzMDQzODY4NSwiaWF0IjoxNjMwNDM3Nzg1fQ.JOgaS7g5pC5xX9CLASVdTeg6FHxfEC7QlKehpDAo0v1ayvxdbAPmD7F27sWsIlCRgX6lb3SMISB5CrVgrBCL4hJ1Z-WhEmba2Pi5NEUCJNnN0y6deattbYGlHL3l3WJx3MAxhjPHvDR-CP3ofr3Mmw1X9bNTWKWg0K46X9EcsYwCrfEMZ9_MaFtnVlDpuXY2XDovgbPzLZAwMLhy5YQrHYo0J8ah9nDP16t-y0QHx864vW8YQ4Wt7FJw_wBsRLmI3ynYjkWFBld9XgvFqImkskNrA9q99Hlji-6vxtFwM2xRUw6Z6Geyqc2GiYtSlpyHW_UvoGQNWR3vPsg4XM3XmQ]}
//        LinkedMultiValueMap(responseCookies) -> {X-Auth=[eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJuZXdAZXhhbXBsZS5jb203NDk5NTA5NTYiLCJpc3MiOiJpZGVudGl0eSIsImV4cCI6MTYzMDQzODY4NSwiaWF0IjoxNjMwNDM3Nzg1fQ.JOgaS7g5pC5xX9CLASVdTeg6FHxfEC7QlKehpDAo0v1ayvxdbAPmD7F27sWsIlCRgX6lb3SMISB5CrVgrBCL4hJ1Z-WhEmba2Pi5NEUCJNnN0y6deattbYGlHL3l3WJx3MAxhjPHvDR-CP3ofr3Mmw1X9bNTWKWg0K46X9EcsYwCrfEMZ9_MaFtnVlDpuXY2XDovgbPzLZAwMLhy5YQrHYo0J8ah9nDP16t-y0QHx864vW8YQ4Wt7FJw_wBsRLmI3ynYjkWFBld9XgvFqImkskNrA9q99Hlji-6vxtFwM2xRUw6Z6Geyqc2GiYtSlpyHW_UvoGQNWR3vPsg4XM3XmQ]}
        println("LinkedMultiValueMap(mapOf-> " + LinkedMultiValueMap(mapOf("X-Auth" to listOf(xAuth))))
//            .block() ?: throw RuntimeException("Should have gotten a login response")

//        val responseCookies = loginResponse.cookies()
//            .map { it.key to it.value.map { cookie -> cookie.value } }
//            .toMap()

//        responseCookies -> {X-Auth=[eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJuZXdAZXhhbXBsZS5jb203NjA5MTYxNjYiLCJpc3MiOiJpZGVudGl0eSIsImV4cCI6MTYzMDQzNzI4NiwiaWF0IjoxNjMwNDM2Mzg2fQ.iV7Bc1t76pYzu3cl3mu87JOvr2Cldxtpda-ELfWfRwiwHqy6ppahb0xQSs5q3TxHuidPbwBihgqCCDBohYL4mAU_VhEDc6boeT4_NNtYjLuwrFFXtj6rpRrKeRClpZJFp_DQn2Si9WSNZ1rOWJbSagsTxIhvHjUQKqx1-Dh6ja8yWVn5l4GMceS1qHLwAQK4v42BmsPL6gYIg68YtqRP9sfHZ2W-Ru6K-Hw0ZR8HFAgQLa88dnVTJKgXCAWA-ipY6qm6cHY-1398ipd4gyRVcw9qpZBnep4Kvaco2B6zpodP1ldXbcKIQrY6qA6jmb2QMg-p8pe9Fhvttq6RqQT8RA]}
//        println("responseCookies -> $responseCookies")
        webClient
            .get()
            .uri("/user")
//             .cookies { it.addAll(LinkedMultiValueMap(responseCookies)) }
             .cookies { it.addAll(LinkedMultiValueMap(mapOf("X-Auth" to listOf(xAuth)))) }
////            .cookies { it.add("X-Auth",xAuth) }
            .exchange()
            .expectStatus().isOk
//            .expectBody(LoggedUserResponse::class.java).isEqualTo(LoggedUserResponse(name = "user test", email = email))

        // assert
//        assertThat(response?.statusCode())
//            .isEqualTo(HttpStatus.OK)
//
//        assertThat(response?.bodyToMono(LoggedUserResponse::class.java)?.block())
//            .isEqualTo(LoggedUserResponse(name = "user test", email = email))
    }

}
