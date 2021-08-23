package com.andreformento.money.user.security.repository

import com.andreformento.money.user.UserId
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface UserAuthPasswordRepository : CoroutineCrudRepository<UserAuthPasswordEntity, UserId> {

    @Modifying
    @Query("insert into user_auth_password (user_id, user_password) values (:user_id, :user_password)")
    suspend fun create(
        @Param("user_id") userId: UserId,
        @Param("user_password") userPassword: String,
    ): Int

    @Modifying
    @Query("update user_auth_password set user_password = :user_password where user_id = :user_id")
    suspend fun update(
        @Param("user_id") userId: UserId,
        @Param("user_password") userPassword: String,
    ): Int

    @Query("""
        SELECT auth.*
          FROM user_auth_password auth
    INNER JOIN users u on u.id = auth.user_id
         WHERE u.email = :user_email
        """)
    suspend fun getUnsafeByEmail(@Param("user_email") userEmail: String): UserAuthPasswordEntity?

}
