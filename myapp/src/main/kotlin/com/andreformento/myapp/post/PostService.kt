package com.andreformento.myapp.post

import com.andreformento.myapp.post.repository.Posts
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService(private val posts: Posts) {

    suspend fun all(): Flow<Post> {
        return posts.findAll()
    }

    suspend fun create(post: Post): Post {
        // TODO authorization
        return posts.save(post)
    }

    suspend fun getById(postId: UUID): Post? {
        // TODO authorization
        return posts.findOne(postId)
    }

    suspend fun update(postId: UUID, post: Post): Int {
        // TODO authorization
        return posts.update(id = postId, title = post.title!!, content = post.content!!)
    }

    suspend fun delete(postId: UUID) {
        // TODO delete comments too
        // TODO only hide
        return posts.deleteById(postId)
    }

}
