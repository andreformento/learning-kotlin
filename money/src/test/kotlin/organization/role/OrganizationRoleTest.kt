package com.andreformento.money.organization.role

import com.andreformento.money.organization.Organization
import com.andreformento.money.user.User
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class OrganizationRoleTest {

    private val organization = Organization(id = UUID.randomUUID(), name = "name org", description = "desc")
    private val otherOrganization = Organization(id = UUID.randomUUID(), name = "other org", description = "a")

    private val ownerUser = User(id = UUID.randomUUID(), name = "owner", email = "o")
    private val ownerOrganizationRole = OrganizationRole(
        id = UUID.randomUUID(),
        organization = organization,
        user = ownerUser,
        role = Role.OWNER,
    )
    private val ownerOfOtherOrganizationRole = OrganizationRole(
        id = UUID.randomUUID(),
        organization = otherOrganization,
        user = ownerUser,
        role = Role.OWNER,
    )

    private val sharedUser = User(id = UUID.randomUUID(), name = "shared", email = "s")
    private val sharedOrganizationRole = OrganizationRole(
        id = UUID.randomUUID(),
        organization = organization,
        user = sharedUser,
        role = Role.ADMIN,
    )

    @Test
    fun `should allow remove self organization role when is not owner`() {
        assertTrue(sharedOrganizationRole.canBeRemovedBy(sharedOrganizationRole))
    }

    @Test
    fun `should deny remove self organization role when is owner`() {
        assertFalse(ownerOrganizationRole.canBeRemovedBy(ownerOrganizationRole))
    }

    @Test
    fun `should allow remove organization role from other user when is owner`() {
        assertTrue(sharedOrganizationRole.canBeRemovedBy(ownerOrganizationRole))
    }

    @Test
    fun `should deny remove organization role from other user when is not owner`() {
        assertFalse(ownerOrganizationRole.canBeRemovedBy(sharedOrganizationRole))
    }

    @Test
    fun `should deny remove organization from other organization role`() {
        assertFalse(ownerOrganizationRole.canBeRemovedBy(ownerOfOtherOrganizationRole))
    }

}
