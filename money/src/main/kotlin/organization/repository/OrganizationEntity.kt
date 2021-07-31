package com.andreformento.money.organization.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationCreation
import com.andreformento.money.organization.OrganizationId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("organization")
data class OrganizationEntity(
    @Id val id: OrganizationId? = null,
    @Column("name") val name: String,
    @Column("description") val description: String
) {
    constructor(organizationCreation: OrganizationCreation) : this(name = organizationCreation.name, description = organizationCreation.description)

    fun toModel(): Organization {
        return Organization(this.id!!, this.name, this.description)
    }
}
