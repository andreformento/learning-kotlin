package com.andreformento.money.organization.role

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.user.User
import com.andreformento.money.user.UserId

import java.util.*

typealias OrganizationRoleId = UUID

fun String.toOrganizationRoleId(): OrganizationRoleId {
    return OrganizationRoleId.fromString(this)
}

enum class Role {
    OWNER,
    ADMIN,
}

class OrganizationRole(
    val id: OrganizationRoleId,
    val organization: Organization,
    val user: User,
    val role: Role
)

class OrganizationRoleCreation(
    val organizationId: OrganizationId,
    val userId: UserId,
    val role: Role
)

class OrganizationRoleCreated(
    val id: OrganizationRoleId,
    val organizationId: OrganizationId,
    val userId: UserId,
    val role: Role
)
