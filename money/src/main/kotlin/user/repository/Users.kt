package com.andreformento.money.user.repository

import User
import UserId
import com.andreformento.money.user.UserCreation
import com.andreformento.money.user.repository.UserEntity
import com.andreformento.money.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Users internal constructor(private val userRepository: UserRepository) {

    fun findAll(): Flow<User> {
        return userRepository.findAll().map(UserEntity::toModel)
    }

    suspend fun findOne(id: UserId): User? {
        return userRepository.findOne(id)?.toModel()
    }

    suspend fun update(user: User): Int {
        return userRepository.update(user.id, user.name, user.email)
    }

    suspend fun save(userCreation: UserCreation): User {
        return userRepository.save(UserEntity(userCreation)).toModel()
    }

    suspend fun deleteById(id: UserId) {
        return userRepository.deleteById(id)
    }

}
