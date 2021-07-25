package com.andreformento.money.user

import com.andreformento.money.user.repository.Users
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService(private val users: Users) {

    suspend fun all(): Flow<User> {
        return users.findAll()
    }

    suspend fun create(userCreation: UserCreation): User {
        // TODO authorization
        return users.save(userCreation)
    }

    suspend fun getById(userId: UserId): User? {
        // TODO authorization
        return users.findOne(userId)
    }

    suspend fun update(userId: UserId, user: User): Int {
        // TODO authorization
        return users.update(User(userId, user.name, user.email))
    }

    suspend fun delete(userId: UserId) {
        // TODO delete comments too
        // TODO only hide
        return users.deleteById(userId)
    }

}
