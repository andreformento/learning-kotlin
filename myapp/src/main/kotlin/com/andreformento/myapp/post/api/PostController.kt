package com.andreformento.myapp.post.api

import com.andreformento.myapp.post.Post
import com.andreformento.myapp.post.PostCreation
import com.andreformento.myapp.post.PostService
import com.andreformento.myapp.post.toPostId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/posts", produces = ["application/json"])
class PostController(private val postService: PostService) {

    @GetMapping
    suspend fun all(): Flow<Post> {
        return postService.all()
    }

    @PostMapping
    suspend fun create(@RequestBody postCreation: PostCreation): ResponseEntity<Post> {
        val createdPost = postService.create(postCreation)
        return ResponseEntity.created(URI.create("/posts/${createdPost.id}")).body(createdPost)
    }

    @GetMapping("/{post-id}")
    suspend fun getById(@PathVariable("post-id") postId: String): ResponseEntity<Post> {
        println("path variable::$postId")
        val foundPost = postService.getById(postId.toPostId())
        println("found post:$foundPost")
        return when {
            foundPost != null -> ResponseEntity.ok(foundPost)
            else -> ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{post-id}")
    suspend fun update(
        @PathVariable("post-id") postId: String,
        @RequestBody post: Post
    ): ResponseEntity<Any> {
        val updateResult = postService.update(postId = postId.toPostId(), post = post)
        println("updateResult -> $updateResult")
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{post-id}")
    suspend fun delete(@PathVariable("post-id") postId: String): ResponseEntity<Any> {
        val deletedCount = postService.delete(postId.toPostId())
        println("$deletedCount posts deleted")
        return ResponseEntity.noContent().build()
    }

}
