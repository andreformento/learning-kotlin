package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.role.OrganizationRoleId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface OrganizationRoleRepository : CoroutineCrudRepository<OrganizationRoleEntity, OrganizationRoleId> {

}
