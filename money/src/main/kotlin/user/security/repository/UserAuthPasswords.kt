package com.andreformento.money.user.security.repository

import com.andreformento.money.user.security.UserAuthPasswordChange
import com.andreformento.money.user.security.UserAuthPasswordCreation
import org.springframework.stereotype.Repository

@Repository
class UserAuthPasswords internal constructor(private val userAuthPasswordRepository: UserAuthPasswordRepository) {

    suspend fun updatePassword(userAuthPasswordChange: UserAuthPasswordChange): Int =
        userAuthPasswordRepository.update(userAuthPasswordChange.userId, userAuthPasswordChange.password)

    suspend fun createPassword(userAuthPasswordCreation: UserAuthPasswordCreation): Int =
        userAuthPasswordRepository.insert(
            userId = userAuthPasswordCreation.userId,
            userPassword = userAuthPasswordCreation.password
        )

    suspend fun getUnsafeByEmail(userEmail: String): UserAuthPasswordEntity? {
        val unsafeByEmail = userAuthPasswordRepository.getUnsafeByEmail(userEmail)
        if (unsafeByEmail == null) {
            // TODO send invalid user not found metric
        }
        return unsafeByEmail
    }

}
