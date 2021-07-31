package com.andreformento.money.organization.api

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationCreation
import com.andreformento.money.organization.OrganizationFacade
import com.andreformento.money.organization.toOrganizationId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/organizations", produces = ["application/json"])
class OrganizationControllerV1(private val organizationFacade: OrganizationFacade) {

    @GetMapping
    suspend fun all(): Flow<Organization> {
        return organizationFacade.all()
    }

    @PostMapping
    suspend fun create(@RequestBody organizationCreation: OrganizationCreation): ResponseEntity<Organization> {
        val createdOrganization = organizationFacade.create(organizationCreation)
        return ResponseEntity.created(URI.create("/organizations/${createdOrganization.id}")).body(createdOrganization)
    }

    @GetMapping("/{organization-id}")
    suspend fun getById(@PathVariable("organization-id") organizationId: String): ResponseEntity<Organization> {
        println("path variable::$organizationId")
        val foundOrganization = organizationFacade.getById(organizationId.toOrganizationId())
        println("found organization:$foundOrganization")
        return when {
            foundOrganization != null -> ResponseEntity.ok(foundOrganization)
            else -> ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{organization-id}")
    suspend fun update(
        @PathVariable("organization-id") organizationId: String,
        @RequestBody organization: Organization
    ): ResponseEntity<Any> {
        val updateResult = organizationFacade.update(organizationId = organizationId.toOrganizationId(), organization = organization)
        println("updateResult -> $updateResult")
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{organization-id}")
    suspend fun delete(@PathVariable("organization-id") organizationId: String): ResponseEntity<Any> {
        val deletedCount = organizationFacade.delete(organizationId.toOrganizationId())
        println("$deletedCount organizations deleted")
        return ResponseEntity.noContent().build()
    }

}
