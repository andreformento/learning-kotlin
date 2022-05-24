package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRole
import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.organization.role.OrganizationRoleId
import com.andreformento.money.user.UserId
import org.springframework.stereotype.Repository

@Repository
class OrganizationRoles internal constructor(
    private val organizationRoleRepository: OrganizationRoleRepository,
    private val fullOrganizationRoleRepository: FullOrganizationRoleRepository,
) {

    suspend fun save(organizationCreation: OrganizationRoleCreation): OrganizationRoleCreated =
        OrganizationRoleEntity(organizationCreation)
            .also {
                organizationRoleRepository.save(
                    organizationRoleId = it.id,
                    organizationId = it.organizationId,
                    userId = it.userId,
                    organizationRole = it.organizationRole,
                )
            }
            .toCreated()

    suspend fun delete(userId: UserId, organizationId: OrganizationId, organizationRoleId: OrganizationRoleId) =
        organizationRoleRepository.delete(userId = userId, organizationId = organizationId, organizationRoleId = organizationRoleId)

    suspend fun getUnsafeUserOrganization(userEmail: String, organizationId: OrganizationId): OrganizationRole? =
        fullOrganizationRoleRepository
            .getUnsafeUserOrganization(organizationId = organizationId, userEmail = userEmail)
            ?.toModel()

    suspend fun getUnsafeUserOrganization(organizationRoleId: OrganizationRoleId, organizationId: OrganizationId): OrganizationRole? =
        fullOrganizationRoleRepository
            .getUnsafeUserOrganization(organizationId = organizationId, organizationRoleId = organizationRoleId)
            ?.toModel()

}
