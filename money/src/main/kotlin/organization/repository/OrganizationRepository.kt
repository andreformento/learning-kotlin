package com.andreformento.money.organization.repository

import com.andreformento.money.organization.OrganizationId
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface OrganizationRepository : CoroutineCrudRepository<OrganizationEntity, OrganizationId> {

    @Query("SELECT * FROM organization WHERE ID = :id")
    suspend fun findOne(@Param("id") id: OrganizationId): OrganizationEntity?

    @Modifying
    @Query("update organization set name = :name, description = :description where id = :id")
    suspend fun update(
        @Param("id") id: OrganizationId,
        @Param("name") name: String,
        @Param("description") description: String
    ): Int

}
