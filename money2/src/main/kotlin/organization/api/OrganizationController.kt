package com.andreformento.money.organization.api

import com.andreformento.money.organization.*
import com.andreformento.money.user.security.CurrentUserAuthentication
import com.andreformento.money.user.security.CurrentUserOrganizationAuthentication
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
    ): ResponseEntity<CreatedOrganization> {
        val currentUser = authentication.principal
        val createdOrganization = organizationFacade.create(currentUser, organizationRegister)
        return ResponseEntity.created(URI.create("/organizations/${createdOrganization.organization.id}")).body(createdOrganization)
    }

    @GetMapping("/{organization-id}")
    suspend fun getById(
        authentication: CurrentUserOrganizationAuthentication,
        @PathVariable("organization-id") organizationId: String
    ): ResponseEntity<Organization> {
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
        authentication: CurrentUserOrganizationAuthentication,
        @PathVariable("organization-id") organizationId: String,
        @RequestBody organizationRegister: OrganizationRegister
    ): ResponseEntity<Organization> {
        val updateResult = organizationFacade.update(
            organizationId = organizationId.toOrganizationId(),
            organizationRegister = organizationRegister
        )
        println("updateResult -> $updateResult")
        return when {
            updateResult != null -> ResponseEntity.ok(updateResult)
            else -> ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{organization-id}")
    suspend fun delete(
        authentication: CurrentUserOrganizationAuthentication,
        @PathVariable("organization-id") organizationId: String
    ): ResponseEntity<Any> {
        val deletedCount = organizationFacade.delete(organizationId.toOrganizationId())
        println("$deletedCount organizations deleted")
        return ResponseEntity.noContent().build()
    }

}
