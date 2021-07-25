package com.andreformento.money.organization

import com.andreformento.money.organization.repository.Organizations
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class OrganizationService(private val organizations: Organizations) {

    suspend fun all(): Flow<Organization> {
        return organizations.findAll()
    }

    suspend fun create(organizationCreation: OrganizationCreation): Organization {
        // TODO authorization
        return organizations.save(organizationCreation)
    }

    suspend fun getById(organizationId: OrganizationId): Organization? {
        // TODO authorization
        return organizations.findOne(organizationId)
    }

    suspend fun update(organizationId: OrganizationId, organization: Organization): Int {
        // TODO authorization
        return organizations.update(Organization(organizationId, organization.name, organization.description))
    }

    suspend fun delete(organizationId: OrganizationId) {
        // TODO delete comments too
        // TODO only hide
        return organizations.deleteById(organizationId)
    }

}
