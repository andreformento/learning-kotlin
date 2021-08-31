package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.*
import com.andreformento.money.user.User
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

@Table("organization_role")
data class FullOrganizationRoleEntity(
    @Id val id: OrganizationRoleId,
    @Column("organization_id") val organizationId: OrganizationId,
    @Column("organization_name") val organizationName: String,
    @Column("organization_description") val organizationDescription: String,
    @Column("user_id") val userId: UserId,
    @Column("user_name") val userName: String,
    @Column("user_email") val userEmail: String,
    @Column("organization_role") val organizationRole: String
) {

    fun toModel(): OrganizationRole =
        OrganizationRole(
            id = this.id,
            organization = Organization(
                id = organizationId,
                name = organizationName,
                description = organizationDescription,
            ),
            user = User(
                id = userId,
                name = userName,
                email = userEmail,
            ),
            role = Role.valueOf(this.organizationRole.uppercase())
        )

}
