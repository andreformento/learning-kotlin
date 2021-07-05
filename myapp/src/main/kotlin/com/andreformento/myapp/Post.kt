package com.andreformento.myapp

import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/posts", produces = ["application/json"])
class PostController(private val posts: PostRepository) {

    fun String.toUUID(): UUID {
        return UUID.fromString(this)
    }

    @GetMapping
    suspend fun all(): Flow<Post> {
        return this.posts.findAll()
    }

    @PostMapping
    suspend fun create(
        @RequestBody post: Post
    ): ResponseEntity<Post> {
        val createdPost = this.posts.save(post)
        return ResponseEntity.created(URI.create("/posts/${createdPost.id}")).body(createdPost)
    }

    @GetMapping("/{post-id}")
    suspend fun get(@PathVariable("post-id") postId: String): ResponseEntity<Post> {
        println("path variable::$postId")
        val foundPost = this.posts.findOne(postId.toUUID())
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
        val updateResult = this.posts.update(id = postId.toUUID(), title = post.title!!, content = post.content!!)
        println("updateResult -> $updateResult")
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{post-id}")
    suspend fun delete(@PathVariable("post-id") postId: String): ResponseEntity<Any> {
        val deletedCount = this.posts.deleteById(postId.toUUID())
        println("$deletedCount posts deleted")
        return ResponseEntity.noContent().build()
    }

}

@Component
interface PostRepository : CoroutineCrudRepository<Post, UUID> {

    @Query(
        """
        SELECT * FROM post WHERE ID = :id
    """
    )
    suspend fun findOne(@Param("id") id: UUID): Post?


    @Modifying
    @Query("update post set title = :title, content = :content where id = :id")
    suspend fun update(
        @Param("id") id: UUID,
        @Param("title") title: String,
        @Param("content") content: String
    ): Int
}


@Table("post")
data class Post(
    @Id val id: UUID? = null,
    @Column("title") val title: String? = null,
    @Column("content") val content: String? = null
)
