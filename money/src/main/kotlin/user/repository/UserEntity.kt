package com.andreformento.money.user.repository

import User
import com.andreformento.money.user.UserCreation
import UserId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class UserEntity(
    @Id val id: UserId? = null,
    @Column("name") val name: String,
    @Column("email") val email: String
) {
    constructor(userCreation: UserCreation) : this(name = userCreation.name, email = userCreation.email)

    fun toModel(): User {
        return User(this.id!!, this.name, this.email)
    }
}
