package com.andreformento.money.user.security

import com.andreformento.money.user.User
import com.andreformento.money.user.UserId

typealias UserPassword = String

class UserAuthPassword(
    val user: User,
    val password: UserPassword,
)

class UserAuthPasswordCreation(
    val userId: UserId,
    val password: UserPassword,
)

typealias UserAuthPasswordChange = UserAuthPasswordCreation

typealias UserAuthPasswordCreated = UserAuthPasswordCreation

data class UserCredentials(val email: String, val password: UserPassword)

typealias CurrentUser = User

data class LoggedUser(
    val id: UserId,
    val name: String,
    val email: String,
    val signedToken: SignedToken,
) {
    constructor(user: User, signedToken: SignedToken) : this(
        id = user.id,
        name = user.name,
        email = user.email,
        signedToken = signedToken,
    )
}
