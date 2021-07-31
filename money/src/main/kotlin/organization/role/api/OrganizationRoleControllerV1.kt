package com.andreformento.money.organization.role.api

import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.organization.role.OrganizationRoleFacade
import com.andreformento.money.organization.role.toOrganizationRoleId
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.toUserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/organizations/{organization-id}/roles", produces = ["application/json"])
class OrganizationRoleControllerV1(private val organizationRoleFacade: OrganizationRoleFacade) {

    @PostMapping
    suspend fun create(@RequestBody organizationRoleCreation: OrganizationRoleCreation): ResponseEntity<OrganizationRoleCreated> {
        val organizationRoleCreated = organizationRoleFacade.create(organizationRoleCreation)
        return ResponseEntity.created(URI.create("/organizations/${organizationRoleCreated.id}")).body(organizationRoleCreated)
    }

    @DeleteMapping("/{organization-role-id}")
    suspend fun delete(
        @PathVariable("organization-id") organizationId: String,
        @PathVariable("organization-role-id") organizationRoleId: String): ResponseEntity<Any> {
        val deletedCount = organizationRoleFacade.delete(
            organizationRoleId = organizationRoleId.toOrganizationRoleId(),
            organizationId = organizationId.toOrganizationId(),
            userId = "TODO".toUserId(), // TODO get userID
        )
        println("$deletedCount organization roles deleted")
        return ResponseEntity.noContent().build()
    }

}
