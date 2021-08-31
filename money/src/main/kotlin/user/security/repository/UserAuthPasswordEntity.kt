package com.andreformento.money.user.security.repository

import com.andreformento.money.user.UserId
import com.andreformento.money.user.security.UserAuthPasswordCreated
import com.andreformento.money.user.security.UserAuthPasswordCreation
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("user_auth_password")
data class UserAuthPasswordEntity(
    @Column("user_id") @Id val userId: UserId,
    @Column("user_password") val password: String
) {
    constructor(userAuthPasswordCreation: UserAuthPasswordCreation) : this(
        userId = userAuthPasswordCreation.userId,
        password = userAuthPasswordCreation.password
    )

    fun toCreated(): UserAuthPasswordCreated =
        UserAuthPasswordCreated(
            userId = this.userId,
            password = this.password,
        )

}
