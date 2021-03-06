package com.andreformento.money.user.security.api

import com.andreformento.money.user.User
import com.andreformento.money.user.UserRegister
import com.andreformento.money.user.api.LoggedUserResponse
import com.andreformento.money.user.security.TokenCookieHandler
import com.andreformento.money.user.security.UserAuthFacade
import com.andreformento.money.user.security.UserCredentials
import com.andreformento.money.user.security.UserPassword
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

class UserSignupCreation(
    val name: String,
    val email: String,
    val password: UserPassword,
)

class CreatedUser(
    val id: String,
    val name: String,
    val email: String,
)

fun User.toResponse() = CreatedUser(
    id = this.id.toString(),
    name = this.name,
    email = this.email,
)

@RestController
@RequestMapping("/user/auth", produces = ["application/json"])
class UserAuthController(
    val userAuthFacade: UserAuthFacade,
    val tokenCookieHandler: TokenCookieHandler,
) {

    @PostMapping("/signup")
    suspend fun signUp(@RequestBody userSignupCreation: UserSignupCreation): ResponseEntity<CreatedUser> =
        userAuthFacade.signup(
            userRegister = UserRegister(name = userSignupCreation.name, email = userSignupCreation.email),
            userPassword = userSignupCreation.password,
        ).let {
            ResponseEntity
                .created(URI.create("/user/auth/login"))
                .body(it.toResponse())
        }

    @PostMapping("/login")
    suspend fun login(@RequestBody userCredentials: UserCredentials): ResponseEntity<LoggedUserResponse> =
        userAuthFacade
            .login(userCredentials)
            ?.let {
                ResponseEntity
                    .ok()
                    .header("Set-Cookie", tokenCookieHandler.createCookie(it.signedToken).toString())
                    .body(LoggedUserResponse(it.name, it.email))
            }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

}
