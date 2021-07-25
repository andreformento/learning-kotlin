package com.andreformento.money.post.repository

import com.andreformento.money.post.Post
import com.andreformento.money.post.PostCreation
import com.andreformento.money.post.PostId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Posts internal constructor(private val postRepository: PostRepository) {

    fun findAll(): Flow<Post> {
        return postRepository.findAll().map(PostEntity::toModel)
    }

    suspend fun findOne(id: PostId): Post? {
        return postRepository.findOne(id)?.toModel()
    }

    suspend fun update(post: Post): Int {
        return postRepository.update(post.id, post.title, post.content)
    }

    suspend fun save(postCreation: PostCreation): Post {
        return postRepository.save(PostEntity(postCreation)).toModel()
    }

    suspend fun deleteById(id: PostId) {
        return postRepository.deleteById(id)
    }

}
