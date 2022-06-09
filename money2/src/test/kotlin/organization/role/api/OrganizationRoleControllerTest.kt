package com.andreformento.money.organization.role.api

import com.andreformento.money.organization.api.CreatedOrganizationForTest
import com.andreformento.money.organization.api.createOrganization
import com.andreformento.money.organization.api.withUser
import com.andreformento.money.user.api.CreatedUserForTest
import com.andreformento.money.user.api.createUser
import com.andreformento.money.user.security.Oauth2AuthenticationManager
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
    private lateinit var authenticationManager: Oauth2AuthenticationManager

    private lateinit var createdOrganizationForTest: CreatedOrganizationForTest
    private lateinit var ownerUser: CreatedUserForTest
    private lateinit var shareUser: CreatedUserForTest

    @BeforeEach
    fun before() {
        ownerUser = webClient.createUser()
        shareUser = webClient.createUser()
        createdOrganizationForTest =
            webClient.createOrganization(authenticationManager, ownerUser.email, ownerUser.password)
    }

    private fun `validate if user hasn't permission to see organization`(user: CreatedUserForTest) {
        assertNull(
            webClient.withUser(
                authenticationManager,
                email = user.email,
                credentials = user.password,
                organizationId = createdOrganizationForTest.createdOrganization.organization.id
            )
        )
    }

    private fun `validate if user has permission to see organization`(user: CreatedUserForTest) {
        assertNotNull(
            webClient.withUser(
                authenticationManager,
                email = user.email,
                credentials = user.password,
                organizationId = createdOrganizationForTest.createdOrganization.organization.id
            )
        )

    }

    private fun `share organization with other user`(): URI {
        val createdEntityResponse = webClient
            .withUser(
                authenticationManager,
                ownerUser.email,
                ownerUser.password,
                createdOrganizationForTest.createdOrganization.organization.id
            )!!
            .post()
            .uri("${createdOrganizationForTest.location}/roles")
            .bodyValue(OrganizationRoleCreationRequest(userId = shareUser.id))
            .exchange()
            .expectStatus().isCreated
            .returnResult<Any>()

        return createdEntityResponse.responseHeaders.location!!
    }

    private fun `remove share organization from user`(
        organizationRoleUri: URI,
        user: CreatedUserForTest
    ): WebTestClient.ResponseSpec =
        webClient
            .withUser(
                authenticationManager,
                user.email,
                user.password,
                createdOrganizationForTest.createdOrganization.organization.id
            )!!
            .delete()
            .uri(organizationRoleUri)
            .exchange()

    @Test
    fun `should share organization with other user`() {
        `validate if user hasn't permission to see organization`(shareUser)
        `share organization with other user`()
        `validate if user has permission to see organization`(shareUser)
        `validate if user has permission to see organization`(ownerUser)
    }

    @Test
    fun `user can remove shared organization with him`() {
        val organizationRoleUri = `share organization with other user`()
        `validate if user has permission to see organization`(shareUser)
        `remove share organization from user`(organizationRoleUri, shareUser).expectStatus().isNoContent
        `validate if user hasn't permission to see organization`(shareUser)
        `validate if user has permission to see organization`(ownerUser)
    }

    @Test
    fun `shared user can't remove owner of organization`() {
        `share organization with other user`()
        val organizationRoleUri =
            URI("/organizations/${createdOrganizationForTest.createdOrganization.organization.id}/roles/${createdOrganizationForTest.createdOrganization.organizationRole.id}")
        `remove share organization from user`(organizationRoleUri, shareUser)
            .expectStatus().isForbidden
            .expectBody().jsonPath("$.errorMessage").isEqualTo("Owner can't remove shared organization with himself")
        `validate if user has permission to see organization`(shareUser)
        `validate if user has permission to see organization`(ownerUser)
    }

    @Test
    fun `owner user of organization should remove shared organization`() {
        val organizationRoleUri = `share organization with other user`()
        `validate if user has permission to see organization`(shareUser)
        `remove share organization from user`(organizationRoleUri, ownerUser).expectStatus().isNoContent
        `validate if user hasn't permission to see organization`(shareUser)
        `validate if user has permission to see organization`(ownerUser)
    }

    // TODO
//    @Test
    fun `shouldn't remove your own organization share`() {
        // TODO URI without org role id???
        `remove share organization from user`(
            URI("/organizations/$createdOrganizationForTest/roles/$"),
            ownerUser
        ).expectStatus().isBadRequest
    }

}
