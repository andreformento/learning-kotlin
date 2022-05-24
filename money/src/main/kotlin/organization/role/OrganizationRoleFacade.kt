package com.andreformento.money.organization.role

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.repository.OrganizationRoles
import org.springframework.stereotype.Service

sealed interface OrganizationRoleDeleteResult
object OrganizationRoleDeleted : OrganizationRoleDeleteResult
object UserNotFoundForThisOrganizationRole : OrganizationRoleDeleteResult
object OrganizationRoleNotFound : OrganizationRoleDeleteResult
object UserDonTHavePermissionToRemove : OrganizationRoleDeleteResult

@Service
class OrganizationRoleFacade(private val organizationRoles: OrganizationRoles) {

    suspend fun create(organizationCreation: OrganizationRoleCreation): OrganizationRoleCreated =
        organizationRoles.save(organizationCreation)

    // TODO test!
    suspend fun delete(
        organizationId: OrganizationId,
        organizationRoleId: OrganizationRoleId,
        authenticatedOrganizationRole: OrganizationRole,
    ): OrganizationRoleDeleteResult {
        val organizationRole = organizationRoles.getUnsafeUserOrganization(
            organizationRoleId = organizationRoleId,
            organizationId = organizationId
        )

        if (organizationRole == null) return UserNotFoundForThisOrganizationRole

        return if (organizationRole.canBeRemovedBy(authenticatedOrganizationRole)) {
            val deletedCount = organizationRoles.delete(userId = organizationRole.user.id, organizationId = organizationId, organizationRoleId = organizationRoleId)
            if (deletedCount > 0) OrganizationRoleDeleted
            else OrganizationRoleNotFound
        } else {
            UserDonTHavePermissionToRemove
        }
    }

}
