package com.andreformento.myapp.post

import com.andreformento.myapp.post.repository.Posts
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class PostService(private val posts: Posts) {

    suspend fun all(): Flow<Post> {
        return posts.findAll()
    }

    suspend fun create(postCreation: PostCreation): Post {
        // TODO authorization
        return posts.save(postCreation)
    }

    suspend fun getById(postId: PostId): Post? {
        // TODO authorization
        return posts.findOne(postId)
    }

    suspend fun update(postId: PostId, post: Post): Int {
        // TODO authorization
        return posts.update(Post(postId, post.title, post.content))
    }

    suspend fun delete(postId: PostId) {
        // TODO delete comments too
        // TODO only hide
        return posts.deleteById(postId)
    }

}
