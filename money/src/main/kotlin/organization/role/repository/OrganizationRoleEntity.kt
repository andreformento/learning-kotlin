package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.organization.role.OrganizationRoleId
import com.andreformento.money.organization.role.Role
import com.andreformento.money.user.UserId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("organization_role")
data class OrganizationRoleEntity(
    @Id val id: OrganizationRoleId,
    @Column("user_id") val userId: UserId,
    @Column("organization_id") val organizationId: OrganizationId,
    @Column("organization_role") val organizationRole: String
) {

    constructor(organizationRoleCreation: OrganizationRoleCreation) : this(
        id = UUID.randomUUID(),
        userId = organizationRoleCreation.userId,
        organizationId = organizationRoleCreation.organizationId,
        organizationRole = organizationRoleCreation.role.toString().lowercase(),
    )

    fun toCreated(): OrganizationRoleCreated =
        OrganizationRoleCreated(
            id = this.id,
            organizationId = this.organizationId,
            userId = this.userId,
            role = Role.valueOf(this.organizationRole.uppercase())
        )

}
