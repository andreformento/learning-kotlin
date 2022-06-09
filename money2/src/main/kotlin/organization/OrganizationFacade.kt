package com.andreformento.money.organization

import com.andreformento.money.organization.repository.Organizations
import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.organization.role.Role
import com.andreformento.money.organization.role.repository.OrganizationRoles
import com.andreformento.money.user.security.CurrentUser
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

class CreatedOrganization(
    val organization: Organization,
    val organizationRole: OrganizationRoleCreated,
)

@Service
class OrganizationFacade(private val organizations: Organizations, private val organizationRoles: OrganizationRoles) {

    suspend fun getAllFromUser(currentUser: CurrentUser): Flow<Organization> =
        organizations.findAllFromUser(currentUser)

    suspend fun create(currentUser: CurrentUser, organizationRegister: OrganizationRegister): CreatedOrganization =
        organizations
            .save(organizationRegister)
            .let {
                CreatedOrganization(
                    organization = it,
                    organizationRole = organizationRoles.save(OrganizationRoleCreation(it.id, currentUser.id, Role.OWNER))
                )
            }

    suspend fun findById(organizationId: OrganizationId) =
        organizations.findById(organizationId)

    suspend fun update(organizationId: OrganizationId, organizationRegister: OrganizationRegister): Organization? =
        organizations.update(Organization(organizationId, organizationRegister.name, organizationRegister.description))

    suspend fun delete(organizationId: OrganizationId) =
        organizations.deleteById(organizationId)

}
