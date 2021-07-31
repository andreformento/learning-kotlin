package com.andreformento.money.user.security

import org.springframework.stereotype.Component

@Component
class UserAuthValidation {

    suspend fun isValid(userCredentials: UserCredentials, userPassword: UserPassword): Boolean {
        // TODO encrypt this
        val validPassword = userCredentials.password == userPassword
        if (!validPassword) {
            // TODO send wrong password metric
        }
        return validPassword
    }

}
