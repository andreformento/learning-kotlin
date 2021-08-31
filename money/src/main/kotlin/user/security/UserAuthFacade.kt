package com.andreformento.money.user.security

import com.andreformento.money.user.User
import com.andreformento.money.user.UserFacade
import com.andreformento.money.user.UserRegister
import com.andreformento.money.user.security.repository.UserAuthPasswords
import org.springframework.stereotype.Service

@Service
class UserAuthFacade(
    val userFacade: UserFacade,
    val userAuthPasswords: UserAuthPasswords,
    val userAuthValidation: UserAuthValidation,
    val tokenSigner: TokenSigner,
) {

    suspend fun signup(userRegister: UserRegister, userPassword: UserPassword): User {
        val createdUser = userFacade.create(userRegister)
        userAuthPasswords.createPassword(UserAuthPasswordCreation(createdUser.id, userPassword))
        // TODO send metric user created
        return createdUser
    }

    suspend fun login(userCredentials: UserCredentials): LoggedUser? = userAuthPasswords
        .getUnsafeByEmail(userCredentials.email)
        ?.takeIf { userAuthValidation.isValid(userCredentials, it.password) }
        ?.let { userFacade.findById(it.userId) }
        // TODO send metric user login success
        ?.let { LoggedUser(user = it, signedToken = tokenSigner.createToken(it.email)) }

}
