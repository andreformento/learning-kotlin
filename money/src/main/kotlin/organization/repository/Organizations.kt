package com.andreformento.money.organization.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationRegister
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.user.security.CurrentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Organizations internal constructor(private val organizationRepository: OrganizationRepository) {

    suspend fun findAllFromUser(currentUser: CurrentUser): Flow<Organization> {
        return organizationRepository.findAllFromUser(userId = currentUser.id).map(OrganizationEntity::toModel)
    }

    suspend fun findById(id: OrganizationId): Organization? {
        return organizationRepository.findById(id)?.toModel()
    }

    suspend fun update(organization: Organization): Int {
        return organizationRepository.update(organization.id, organization.name, organization.description)
    }

    suspend fun save(organizationRegister: OrganizationRegister): Organization {
        return organizationRepository.save(OrganizationEntity(organizationRegister)).toModel()
    }

    suspend fun deleteById(id: OrganizationId) {
        return organizationRepository.deleteById(id)
    }

}
