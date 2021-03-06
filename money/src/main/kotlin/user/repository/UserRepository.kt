package com.andreformento.money.user.repository

import com.andreformento.money.user.UserId
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface UserRepository : CoroutineCrudRepository<UserEntity, UserId> {

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun findByEmail(@Param("email") email: String): UserEntity?

    @Modifying
    @Query("update users set name = :name, email = :email where id = :id")
    suspend fun update(
        @Param("id") id: UserId,
        @Param("name") name: String,
        @Param("email") email: String
    ): Int

}
