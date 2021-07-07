package com.andreformento.myapp.post.api

import com.andreformento.myapp.post.Post
import com.andreformento.myapp.post.PostService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/posts", produces = ["application/json"])
class PostController(private val postService: PostService) {

    fun String.toUUID(): UUID {
        return UUID.fromString(this)
    }

    @GetMapping
    suspend fun all(): Flow<Post> {
        return postService.all()
    }

    @PostMapping
    suspend fun create(@RequestBody post: Post): ResponseEntity<Post> {
        val createdPost = postService.create(post)
        return ResponseEntity.created(URI.create("/posts/${createdPost.id}")).body(createdPost)
    }

    @GetMapping("/{post-id}")
    suspend fun getById(@PathVariable("post-id") postId: String): ResponseEntity<Post> {
        println("path variable::$postId")
        val foundPost = postService.getById(postId.toUUID())
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
        val updateResult = postService.update(postId = postId.toUUID(), post = post)
        println("updateResult -> $updateResult")
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{post-id}")
    suspend fun delete(@PathVariable("post-id") postId: String): ResponseEntity<Any> {
        val deletedCount = postService.delete(postId.toUUID())
        println("$deletedCount posts deleted")
        return ResponseEntity.noContent().build()
    }

}
