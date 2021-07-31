package com.andreformento.money.organization.role

import com.andreformento.money.organization.Organization
import com.andreformento.money.user.User

enum class Role {
    OWNER,
    ADMIN,
}

class OrganizationRole(
    val organization: Organization,
    val user: User,
    val role: Role
)
