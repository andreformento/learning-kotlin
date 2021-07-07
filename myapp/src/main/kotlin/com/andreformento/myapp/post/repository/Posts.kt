package com.andreformento.myapp.post.repository

import com.andreformento.myapp.post.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class Posts internal constructor(private val postRepository: PostRepository) {

    fun findAll(): Flow<Post> {
        return postRepository.findAll().map(PostEntity::toModel)
    }

    suspend fun findOne(id: UUID): Post? {
        return postRepository.findOne(id)?.toModel()
    }

    suspend fun update(post: Post): Int {
        return postRepository.update(post.id, post.title, post.content)
    }

    suspend fun save(post: Post): Post {
        return postRepository.save(PostEntity(post)).toModel()
    }

    suspend fun deleteById(id: UUID) {
        return postRepository.deleteById(id)
    }

}
