package com.andreformento.money.organization.role.api

import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.organization.role.OrganizationRoleFacade
import com.andreformento.money.organization.role.Role
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.UserId
import com.andreformento.money.user.security.CurrentUserOrganizationAuthentication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

data class OrganizationRoleCreationRequest(
    val role: Role,
    val userId: UserId,
    )

@RestController
@RequestMapping("/organizations/{organization-id}/roles", produces = ["application/json"])
class OrganizationRoleController(private val organizationRoleFacade: OrganizationRoleFacade) {

    @PostMapping
    suspend fun create(
        authentication: CurrentUserOrganizationAuthentication,
        @RequestBody organizationRoleCreationRequest: OrganizationRoleCreationRequest,
    ): ResponseEntity<OrganizationRoleCreated> {
        val organizationId = authentication.principal.organization.id

        val organizationRoleCreated = organizationRoleFacade.create(
            OrganizationRoleCreation(
                userId = organizationRoleCreationRequest.userId,
                organizationId = organizationId,
                role = organizationRoleCreationRequest.role,
            )
        )
        return ResponseEntity
            .created(URI.create("/organizations/${organizationId}/roles/${organizationRoleCreated.id}"))
            .body(organizationRoleCreated)
    }

    @DeleteMapping("/{organization-role-id}")
    suspend fun delete(
        authentication: CurrentUserOrganizationAuthentication,
        @PathVariable("organization-id") organizationId: String,
    ): ResponseEntity<Any> {
        val deletedCount = organizationRoleFacade.delete(
            userId = authentication.principal.id,
            organizationId = organizationId.toOrganizationId(),
        )
        println("$deletedCount organization roles deleted")

        return if (deletedCount > 0) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

}
