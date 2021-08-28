package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRoleId
import com.andreformento.money.user.UserId
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface FullOrganizationRoleRepository :
    CoroutineCrudRepository<FullOrganizationRoleEntity, OrganizationRoleId> {

    @Query(
        """
        SELECT orr.organization_id as id,
               o.id as organization_id,
               o.name as organization_name,
               o.description as organization_description,
               u.id as user_id,
               u.name as user_name,
               u.email as user_email,
               cast(orr.organization_role as varchar) as organization_role
          FROM organization o
    INNER JOIN organization_role orr on orr.organization_id = o.id
    INNER JOIN users u on u.id = orr.user_id
         WHERE orr.user_id = :user_id
           AND o.id = :organization_id
        """
    )
    suspend fun getUnsafeUserOrganization(
        @Param("user_id") userId: UserId,
        @Param("organization_id") organizationId: OrganizationId,
    ): FullOrganizationRoleEntity?

}
