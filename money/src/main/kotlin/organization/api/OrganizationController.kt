package com.andreformento.money.organization.api

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationFacade
import com.andreformento.money.organization.OrganizationRegister
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.security.CurrentUserAuthentication
import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/organizations", produces = ["application/json"])
class OrganizationController(private val organizationFacade: OrganizationFacade) {

    @GetMapping
    suspend fun getAllFromUser(authentication: CurrentUserAuthentication): Flow<Organization> {
        val currentUser = authentication.principal

        return organizationFacade.getAllFromUser(currentUser)
    }

    @PostMapping
    suspend fun create(
        authentication: CurrentUserAuthentication,
        @RequestBody organizationRegister: OrganizationRegister
    ): ResponseEntity<Organization> {
        val currentUser = authentication.principal
        val createdOrganization = organizationFacade.create(currentUser, organizationRegister)
        return ResponseEntity.created(URI.create("/organizations/${createdOrganization.id}")).body(createdOrganization)
    }

    @GetMapping("/{organization-id}")
    suspend fun getById(
        authentication: CurrentUserAuthentication,
        @PathVariable("organization-id") organizationId: String
    ): ResponseEntity<Organization> {
        val currentUser = authentication.principal
        println("path variable::$organizationId")
        val foundOrganization = organizationFacade.findById(organizationId.toOrganizationId())
        println("found organization:$foundOrganization")
        return when {
            foundOrganization != null -> ResponseEntity.ok(foundOrganization)
            else -> ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{organization-id}")
    suspend fun update(
        authentication: CurrentUserAuthentication,
        @PathVariable("organization-id") organizationId: String,
        @RequestBody organization: Organization
    ): ResponseEntity<Any> {
        val currentUser = authentication.principal
        val updateResult =
            organizationFacade.update(
                organizationId = organizationId.toOrganizationId(),
                organization = organization
            )
        println("updateResult -> $updateResult")
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{organization-id}")
    suspend fun delete(
        authentication: CurrentUserAuthentication,
        @PathVariable("organization-id") organizationId: String
    ): ResponseEntity<Any> {
        val currentUser = authentication.principal
        val deletedCount = organizationFacade.delete(organizationId.toOrganizationId())
        println("$deletedCount organizations deleted")
        return ResponseEntity.noContent().build()
    }

}
