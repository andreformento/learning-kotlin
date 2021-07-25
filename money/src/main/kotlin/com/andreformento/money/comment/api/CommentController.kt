package com.andreformento.money.comment.api

import com.andreformento.money.comment.Comment
import com.andreformento.money.comment.CommentService
import com.andreformento.money.comment.toCommentId
import com.andreformento.money.post.toPostId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts/{post-id}/comments", produces = ["application/json"])
class CommentController(private val commentService: CommentService) {

    @GetMapping
    suspend fun getCommentsByPost(@PathVariable("post-id") postId: String): ResponseEntity<Flow<Comment>> {
        println("path postId::$postId")
        return ResponseEntity.ok(this.commentService.getCommentsByPost(postId.toPostId()))
    }

    @GetMapping("/{comment-id}")
    suspend fun getCommentByIdAndPost(
        @PathVariable("post-id") postId: String,
        @PathVariable("comment-id") commentId: String
    ): ResponseEntity<Comment> {
        println("path postId::$postId")
        println("path commentId::$commentId")
        return ResponseEntity.ok(this.commentService.getCommentByIdAndPost(postId.toPostId(), commentId.toCommentId()))
    }
}
