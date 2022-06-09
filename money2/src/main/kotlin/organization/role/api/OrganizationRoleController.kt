package com.andreformento.money.organization.role.api

import com.andreformento.money.organization.role.*
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.UserId
import com.andreformento.money.user.security.CurrentUserOrganizationAuthentication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

data class OrganizationRoleCreationRequest(
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
                role = Role.ADMIN,
            )
        )
        return ResponseEntity
            .created(URI.create("/organizations/${organizationId}/roles/${organizationRoleCreated.id}"))
            .body(organizationRoleCreated)
    }

    @DeleteMapping("/{organization-role-id}")
    suspend fun delete(
        authentication: CurrentUserOrganizationAuthentication,
        @PathVariable("organization-id") organizationParam: String,
        @PathVariable("organization-role-id") organizationRoleParam: String,
    ): ResponseEntity<Any> =
        organizationRoleFacade
            .delete(
                organizationId = organizationParam.toOrganizationId(),
                organizationRoleId = organizationRoleParam.toOrganizationRoleId(),
                authenticatedOrganizationRole = authentication.principal
            ).let {
                when (it) {
                    is OrganizationRoleDeleted -> ResponseEntity.noContent().build()
                    is UserNotFoundForThisOrganizationRole -> ResponseEntity.notFound().build()
                    is OrganizationRoleNotFound -> ResponseEntity.notFound().build()
                    is UserDonTHavePermissionToRemove -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(object {
                        val errorMessage = "Owner can't remove shared organization with himself"
                    })
                }
            }

}
