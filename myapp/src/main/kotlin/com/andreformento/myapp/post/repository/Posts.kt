package com.andreformento.myapp.post.repository

import com.andreformento.myapp.post.Post
import kotlinx.coroutines.flow.Flow
import java.util.*

interface Posts {

    fun findAll(): Flow<Post>

    suspend fun findOne(id: UUID): Post?

    suspend fun update(id: UUID, title: String, content: String): Int

    suspend fun save(entity: Post): Post

    suspend fun deleteById(id: UUID)

}
