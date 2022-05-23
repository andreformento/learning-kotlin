package com.andreformento.money.organization.role.api

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.OrganizationRegister
import com.andreformento.money.organization.api.createOrganization
import com.andreformento.money.organization.api.withUser
import com.andreformento.money.organization.role.Role
import com.andreformento.money.organization.role.toOrganizationRoleId
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.security.TokenAuthenticationManager
import com.andreformento.money.user.toUserId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.net.URI

@SpringBootTest
@AutoConfigureWebTestClient
class OrganizationRoleControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var tokenAuthenticationManager: TokenAuthenticationManager

    private lateinit var organizationId: OrganizationId

    @BeforeEach
    fun before() {
        organizationId = webClient.createOrganization(
            tokenAuthenticationManager,
            "iron-maiden@evil.hell",
            "pass",
        )
    }

    @Test
    fun `share organization with other user`() {
        assertNull(
            webClient.withUser(
                tokenAuthenticationManager,
                "share@blah.io",
                "pass",
                organizationId
            )
        )

        val createdEntityResponse = webClient
            .withUser(tokenAuthenticationManager, "iron-maiden@evil.hell", "pass", organizationId)!!
            .post()
            .uri("/organizations/$organizationId/roles")
            .bodyValue(OrganizationRoleCreationRequest(
                role = Role.ADMIN,
                userId = "f25a42ca-dd73-4c82-9d9c-b7da6d50d0e9".toUserId(),
            ))
            .exchange()
            .expectStatus().isCreated
            .returnResult<Any>()

        val organizationLocation: URI = createdEntityResponse.responseHeaders.location!!
        organizationLocation.path.substring(organizationLocation.path.lastIndexOf('/') + 1).toOrganizationRoleId()

        assertNotNull(
            webClient.withUser(
                tokenAuthenticationManager,
                "share@blah.io",
                "pass",
                organizationId
            )
        )
    }

    @Test
    fun `remove share organization with other user`() {

        TODO()
    }

    @Test
    fun `don't remove your own organization share`() {
        TODO()
    }


}
