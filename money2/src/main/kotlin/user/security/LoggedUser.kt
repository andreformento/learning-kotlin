package com.andreformento.money.user.security

import com.andreformento.money.user.User
import com.andreformento.money.user.UserId

typealias CurrentUser = User

data class LoggedUser(
    val id: UserId,
    val name: String,
    val email: String,
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        email = user.email,
    )
}
