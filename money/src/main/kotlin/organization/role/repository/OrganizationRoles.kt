package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.user.UserId
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class OrganizationRoles internal constructor(private val organizationRoleRepository: OrganizationRoleRepository) {

    suspend fun save(organizationCreation: OrganizationRoleCreation) =
        OrganizationRoleEntity(organizationCreation).let {
            println(it)
            organizationRoleRepository.create(
                organizationRoleId = it.id,
                organizationId = it.organizationId,
                organizationRole = it.organizationRole,
                userId = it.userId,
            )
        }//.toCreated()

    suspend fun delete(userId: UserId, organizationId: OrganizationId) =
        organizationRoleRepository.delete(userId, organizationId)

}
