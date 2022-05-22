package com.andreformento.money.organization.api

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.security.TokenAuthenticationManager
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

fun WebTestClient.withUser(
    tokenAuthenticationManager: TokenAuthenticationManager,
    email: String,
    credentials: String,
    organizationId: OrganizationId? = null,
): WebTestClient {
    val authentication =
        runBlocking { tokenAuthenticationManager.getAuthentication(email, credentials, organizationId) }!!
    return this.mutateWith(SecurityMockServerConfigurers.mockAuthentication(authentication))
}

@SpringBootTest
@AutoConfigureWebTestClient
class OrganizationControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var tokenAuthenticationManager: TokenAuthenticationManager

    @Test
    fun `can obtain all own user organizations`() {
        webClient
            .withUser(tokenAuthenticationManager, "iron-maiden@evil.hell", "pass")
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(2)
            .contains(
                Organization(id="ae0d9647-e235-481f-baaf-818ad50ba8d8".toOrganizationId(), name="shared-organization", description = "Shared organization"),
                Organization(id="598e6fd7-0d30-43a6-94ca-68e44c2167aa".toOrganizationId(), name="other-organization", description = "Other organization from the same user"),
            )
    }

    @Test
    fun `can obtain shared user organizations`() {
        webClient
            .withUser(tokenAuthenticationManager, "share@blah.io", "pass")
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(1)
            .contains(
                Organization(id="ae0d9647-e235-481f-baaf-818ad50ba8d8".toOrganizationId(), name="shared-organization", description = "Shared organization"),
            )
    }

    @Test
    fun `can obtain no organizations from user without organizations`() {
        webClient
            .withUser(tokenAuthenticationManager, "user@without.org", "pass")
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(0)
    }

}
