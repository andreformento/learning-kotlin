package com.andreformento.money.organization.role

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.repository.OrganizationRoles
import com.andreformento.money.user.UserId
import org.springframework.stereotype.Service

@Service
class OrganizationRoleFacade(private val organizationRoles: OrganizationRoles) {

    suspend fun create(organizationCreation: OrganizationRoleCreation): OrganizationRoleCreated =
        organizationRoles.save(organizationCreation)

    suspend fun delete(userId: UserId, organizationId: OrganizationId) =
        organizationRoles.delete(userId, organizationId)

}
