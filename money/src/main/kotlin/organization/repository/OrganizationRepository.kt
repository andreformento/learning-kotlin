package com.andreformento.money.organization.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.user.UserId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface OrganizationRepository : CoroutineCrudRepository<OrganizationEntity, OrganizationId> {

    @Query("""
        SELECT o.*
          FROM organization o
    INNER JOIN organization_role orr on orr.organization_id = o.id
         WHERE orr.user_id = :user_id
        """)
    suspend fun findAllFromUser(@Param("user_id") userId: UserId): Flow<OrganizationEntity>

    @Modifying
    @Query("update organization set name = :name, description = :description where id = :id")
    suspend fun update(
        @Param("id") id: OrganizationId,
        @Param("name") name: String,
        @Param("description") description: String
    ): Int

}
