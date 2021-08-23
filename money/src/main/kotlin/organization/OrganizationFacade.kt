package com.andreformento.money.organization

import com.andreformento.money.organization.repository.Organizations
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.organization.role.Role
import com.andreformento.money.organization.role.repository.OrganizationRoles
import com.andreformento.money.user.security.CurrentUser
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class OrganizationFacade(private val organizations: Organizations, private val organizationRoles: OrganizationRoles) {

    suspend fun getAllFromUser(currentUser: CurrentUser): Flow<Organization> {
        return organizations.findAllFromUser(currentUser)
    }

    suspend fun create(currentUser: CurrentUser, organizationRegister: OrganizationRegister): Organization {
        val organizationCreated = organizations.save(organizationRegister)
        organizationRoles.save(OrganizationRoleCreation(organizationCreated.id, currentUser.id, Role.OWNER))
        return organizationCreated
    }

    suspend fun getById(currentUser: CurrentUser, organizationId: OrganizationId): Organization? {
        // TODO authorization
        return organizations.findById(organizationId)
    }

    suspend fun update(currentUser: CurrentUser, organizationId: OrganizationId, organization: Organization): Int {
        // TODO authorization
        return organizations.update(Organization(organizationId, organization.name, organization.description))
    }

    suspend fun delete(currentUser: CurrentUser, organizationId: OrganizationId) {
        // TODO only hide
        return organizations.deleteById(organizationId)
    }

}
