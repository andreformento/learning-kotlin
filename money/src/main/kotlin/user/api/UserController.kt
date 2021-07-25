package com.andreformento.money.user.api

import com.andreformento.money.user.User
import com.andreformento.money.user.UserCreation
import com.andreformento.money.user.UserService
import com.andreformento.money.user.toUserId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users", produces = ["application/json"])
class UserController(private val userService: UserService) {

    @GetMapping
    suspend fun all(): Flow<User> {
        return userService.all()
    }

    @PostMapping
    suspend fun create(@RequestBody userCreation: UserCreation): ResponseEntity<User> {
        val createdUser = userService.create(userCreation)
        return ResponseEntity.created(URI.create("/users/${createdUser.id}")).body(createdUser)
    }

    @GetMapping("/{user-id}")
    suspend fun getById(@PathVariable("user-id") userId: String): ResponseEntity<User> {
        println("path variable::$userId")
        val foundUser = userService.getById(userId.toUserId())
        println("found user:$foundUser")
        return when {
            foundUser != null -> ResponseEntity.ok(foundUser)
            else -> ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{user-id}")
    suspend fun update(
        @PathVariable("user-id") userId: String,
        @RequestBody user: User
    ): ResponseEntity<Any> {
        val updateResult = userService.update(userId = userId.toUserId(), user = user)
        println("updateResult -> $updateResult")
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{user-id}")
    suspend fun delete(@PathVariable("user-id") userId: String): ResponseEntity<Any> {
        val deletedCount = userService.delete(userId.toUserId())
        println("$deletedCount users deleted")
        return ResponseEntity.noContent().build()
    }

}
