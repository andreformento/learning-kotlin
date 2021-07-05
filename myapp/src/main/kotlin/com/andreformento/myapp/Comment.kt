package com.andreformento.myapp

import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/posts/{post-id}/comments", produces = ["application/json"])
class CommentController(private val comments: CommentRepository) {

    fun String.toUUID(): UUID {
        return UUID.fromString(this)
    }

    @GetMapping
    suspend fun getCommentsByPost(@PathVariable("post-id") postId: String): ResponseEntity<Flow<Comment>> {
        println("path postId::$postId")
        return ResponseEntity.ok(this.comments.getCommentsByPost(postId.toUUID()))
    }

    @GetMapping("/{comment-id}")
    suspend fun getCommentByIdAndPost(
        @PathVariable("post-id") postId: String,
        @PathVariable("comment-id") commentId: String
    ): ResponseEntity<Comment> {
        println("path postId::$postId")
        println("path commentId::$commentId")
        return ResponseEntity.ok(this.comments.getCommentByIdAndPost(postId.toUUID(), commentId.toUUID()))
    }
}

@Component
interface CommentRepository : CoroutineCrudRepository<Comment, UUID> {

    @Query("SELECT COUNT(*) FROM comment WHERE post_id = :postId")
    suspend fun countByPostId(@Param("postId") postId: UUID): Long

    @Query("SELECT * FROM comment WHERE post_id = :postId")
    suspend fun getCommentsByPost(@Param("postId") postId: UUID): Flow<Comment>

    @Query("SELECT * FROM comment WHERE id = :commentId AND post_id = :postId")
    suspend fun getCommentByIdAndPost(@Param("postId") postId: UUID, @Param("commentId") commentId: UUID): Comment?

}

@Table("comment")
data class Comment(
    @Id val id: UUID? = null,
    @Column("content") val content: String? = null,
    @Column("post_id") val postId: UUID? = null
)
