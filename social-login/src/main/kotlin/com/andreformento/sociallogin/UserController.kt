package com.andreformento.sociallogin

import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/", produces = ["application/json"])
class UserController {

    @GetMapping
    suspend fun getCurrentUser(a: Authentication):Any {
        println(a.principal)
        return object {
            val a = 0
        }
    }

}
