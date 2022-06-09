package com.andreformento.money.organization.api

import com.andreformento.money.organization.*
import com.andreformento.money.user.security.Oauth2AuthenticationManager
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
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

class CreatedOrganizationForTest(val createdOrganization: CreatedOrganization, val location: URI)

fun WebTestClient.withUser(
    authenticationManager: Oauth2AuthenticationManager,
    email: String,
    credentials: String,
    organizationId: OrganizationId? = null,
): WebTestClient? {
    return runBlocking {
        authenticationManager.getAuthentication(
            email,
            credentials,
            organizationId
        )
    }?.let {
        this.mutateWith(SecurityMockServerConfigurers.mockAuthentication(it))
    }
}

fun WebTestClient.createOrganization(
    authenticationManager: Oauth2AuthenticationManager,
    email: String,
    credentials: String,
    organizationId: OrganizationId? = null,
): CreatedOrganizationForTest {
    // create
    val response = this
        .withUser(authenticationManager, email, credentials, organizationId)!!
        .post()
        .uri("/organizations")
        .bodyValue(OrganizationRegister("my-new-org", "same description"))
        .exchange()
        .expectStatus().isCreated

    val createdEntityResponse = response.returnResult<Organization>()
    val createdOrganization = response.returnResult<CreatedOrganization>().responseBody.blockFirst()!!

    assertThat(createdOrganization.organization.id).isNotNull()
    assertThat(createdOrganization.organization.name).isEqualTo("my-new-org")
    assertThat(createdOrganization.organization.description).isEqualTo("same description")

    return CreatedOrganizationForTest(createdOrganization, createdEntityResponse.responseHeaders.location!!)
}

@SpringBootTest
@AutoConfigureWebTestClient
class OrganizationControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var authenticationManager: Oauth2AuthenticationManager

    @Test
    fun `can obtain all own user organizations`() {
        webClient
            .withUser(authenticationManager, "iron-maiden@evil.hell", "pass")!!
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
                authenticationManager,
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
                    authenticationManager,
                    "share@blah.io",
                    "pass",
                    "598e6fd7-0d30-43a6-94ca-68e44c2167aa".toOrganizationId()
                )
        )
    }

    @Test
    fun `can obtain all shared user organizations`() {
        webClient
            .withUser(authenticationManager, "share@blah.io", "pass")!!
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
            .withUser(authenticationManager, "user@without.org", "pass")!!
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
        val createdOrganizationForTest = webClient.createOrganization(authenticationManager, "manage@org", "pass")

        // read
        webClient
            .withUser(authenticationManager, "manage@org", "pass", createdOrganizationForTest.createdOrganization.organization.id)!!
            .get()
            .uri(createdOrganizationForTest.location)
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(
                Organization(
                    id = createdOrganizationForTest.createdOrganization.organization.id,
                    name = "my-new-org",
                    description = "same description"
                )
            )

        // update
        webClient
            .withUser(authenticationManager, "manage@org", "pass", createdOrganizationForTest.createdOrganization.organization.id)!!
            .put()
            .uri(createdOrganizationForTest.location)
            .bodyValue(OrganizationRegister("my-new-org-updated", "same description-updated"))
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(
                Organization(
                    id = createdOrganizationForTest.createdOrganization.organization.id,
                    name = "my-new-org-updated",
                    description = "same description-updated"
                )
            )

        // TODO test delete
    }

}
