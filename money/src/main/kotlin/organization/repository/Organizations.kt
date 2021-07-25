package com.andreformento.money.organization.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationCreation
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.repository.OrganizationEntity
import com.andreformento.money.organization.repository.OrganizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Organizations internal constructor(private val organizationRepository: OrganizationRepository) {

    fun findAll(): Flow<Organization> {
        return organizationRepository.findAll().map(OrganizationEntity::toModel)
    }

    suspend fun findOne(id: OrganizationId): Organization? {
        return organizationRepository.findOne(id)?.toModel()
    }

    suspend fun update(organization: Organization): Int {
        return organizationRepository.update(organization.id, organization.name, organization.description)
    }

    suspend fun save(organizationCreation: OrganizationCreation): Organization {
        return organizationRepository.save(OrganizationEntity(organizationCreation)).toModel()
    }

    suspend fun deleteById(id: OrganizationId) {
        return organizationRepository.deleteById(id)
    }

}
