package com.andreformento.money.organization.api

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.OrganizationRegister
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.security.TokenAuthenticationManager
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.test.web.reactive.server.returnResult
import java.net.URI

fun WebTestClient.withUser(
    tokenAuthenticationManager: TokenAuthenticationManager,
    email: String,
    credentials: String,
    organizationId: OrganizationId? = null,
): WebTestClient? {
    return runBlocking {
        tokenAuthenticationManager.getAuthentication(
            email,
            credentials,
            organizationId
        )
    }?.let {
        this.mutateWith(SecurityMockServerConfigurers.mockAuthentication(it))
    }
}

fun WebTestClient.createOrganization(
    tokenAuthenticationManager: TokenAuthenticationManager,
    email: String,
    credentials: String,
    organizationId: OrganizationId? = null,
): OrganizationId {
    // create
    val createdEntityResponse = this
        .withUser(tokenAuthenticationManager, email, credentials, organizationId)!!
        .post()
        .uri("/organizations")
        .bodyValue(OrganizationRegister("my-new-org", "same description"))
        .exchange()
        .expectStatus().isCreated
        .returnResult<Any>()

    val organizationLocation: URI = createdEntityResponse.responseHeaders.location!!
    return organizationLocation.path.substring(organizationLocation.path.lastIndexOf('/') + 1).toOrganizationId()
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
            .withUser(tokenAuthenticationManager, "iron-maiden@evil.hell", "pass")!!
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(2)
            .contains(
                Organization(
                    id = "ae0d9647-e235-481f-baaf-818ad50ba8d8".toOrganizationId(),
                    name = "shared-organization",
                    description = "Shared organization"
                ),
                Organization(
                    id = "598e6fd7-0d30-43a6-94ca-68e44c2167aa".toOrganizationId(),
                    name = "other-organization",
                    description = "Other organization from the same user"
                ),
            )
    }

    @Test
    fun `can obtain my own user organization`() {
        webClient
            .withUser(
                tokenAuthenticationManager,
                "iron-maiden@evil.hell",
                "pass",
                "598e6fd7-0d30-43a6-94ca-68e44c2167aa".toOrganizationId()
            )!!
            .get()
            .uri("/organizations/598e6fd7-0d30-43a6-94ca-68e44c2167aa")
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(
                Organization(
                    id = "598e6fd7-0d30-43a6-94ca-68e44c2167aa".toOrganizationId(),
                    name = "other-organization",
                    description = "Other organization from the same user"
                )
            )
    }

    @Test
    fun `can't obtain organization not shared with user`() {
        assertNull(
            webClient
                .withUser(
                    tokenAuthenticationManager,
                    "share@blah.io",
                    "pass",
                    "598e6fd7-0d30-43a6-94ca-68e44c2167aa".toOrganizationId()
                )
        )
    }

    @Test
    fun `can obtain all shared user organizations`() {
        webClient
            .withUser(tokenAuthenticationManager, "share@blah.io", "pass")!!
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(1)
            .contains(
                Organization(
                    id = "ae0d9647-e235-481f-baaf-818ad50ba8d8".toOrganizationId(),
                    name = "shared-organization",
                    description = "Shared organization"
                ),
            )
    }

    @Test
    fun `can't obtain organizations from user without organizations`() {
        webClient
            .withUser(tokenAuthenticationManager, "user@without.org", "pass")!!
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(0)
    }

    @Test
    fun `CRUD - can manage an organization`() {
        // create
        val organizationId = webClient.createOrganization(tokenAuthenticationManager, "manage@org", "pass")!!

        // read
        webClient
            .withUser(tokenAuthenticationManager, "manage@org", "pass", organizationId)!!
            .get()
            .uri("/organizations/$organizationId")
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(Organization(id = organizationId, name = "my-new-org", description = "same description"))

        // update
        webClient
            .withUser(tokenAuthenticationManager, "manage@org", "pass", organizationId)!!
            .put()
            .uri("/organizations/$organizationId")
            .bodyValue(OrganizationRegister("my-new-org-updated", "same description-updated"))
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(
                Organization(
                    id = organizationId,
                    name = "my-new-org-updated",
                    description = "same description-updated"
                )
            )

        // TODO test delete
    }

}
