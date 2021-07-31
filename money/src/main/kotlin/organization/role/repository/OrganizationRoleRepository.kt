package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRoleId
import com.andreformento.money.user.UserId
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface OrganizationRoleRepository : CoroutineCrudRepository<OrganizationRoleEntity, OrganizationRoleId> {

    @Modifying
    @Query("delete from organization_role where user_id = :user_id and organization_id = :organization_id ")
    suspend fun delete(
        @Param("user_id") userId: UserId,
        @Param("organization_id") organizationId: OrganizationId,
    ): Int

}
