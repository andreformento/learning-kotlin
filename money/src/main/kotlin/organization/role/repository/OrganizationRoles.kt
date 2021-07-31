package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import org.springframework.stereotype.Repository

@Repository
class OrganizationRoles internal constructor(private val organizationRoleRepository: OrganizationRoleRepository) {

    suspend fun save(organizationCreation: OrganizationRoleCreation): OrganizationRoleCreated {
        return organizationRoleRepository.save(OrganizationRoleEntity(organizationCreation)).toCreated()
    }

    suspend fun deleteById(id: OrganizationId) {
        return organizationRoleRepository.deleteById(id)
    }

}
