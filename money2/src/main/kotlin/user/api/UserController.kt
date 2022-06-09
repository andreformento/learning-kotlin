package com.andreformento.money.user.api

import com.andreformento.money.user.User
import com.andreformento.money.user.UserFacade
import com.andreformento.money.user.UserRegister
import com.andreformento.money.user.security.CurrentUserAuthentication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoggedUserResponse(val name: String, val email: String) {
    constructor(user: User) : this(name = user.name, email = user.email)
}

@RestController
@RequestMapping("/user", produces = ["application/json"])
class UserController(
    private val userFacade: UserFacade,
) {

    @GetMapping
    suspend fun getCurrentUser(authentication: CurrentUserAuthentication): ResponseEntity<LoggedUserResponse> {
        val currentUser = authentication.principal

        println("path variable::${currentUser.id}")
        val foundUser = userFacade.findById(currentUser.id)
        println("found user:$foundUser")
        return when {
            foundUser != null -> ResponseEntity.ok(LoggedUserResponse(foundUser))
            else -> ResponseEntity.notFound().build()
        }
    }

}
