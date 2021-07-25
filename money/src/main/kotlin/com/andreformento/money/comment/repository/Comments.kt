package com.andreformento.money.comment.repository

import com.andreformento.money.comment.Comment
import com.andreformento.money.comment.CommentId
import com.andreformento.money.post.PostId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Comments internal constructor(private val commentRepository: CommentRepository) {

    suspend fun getCommentsByPost(postId: PostId): Flow<Comment> {
        return commentRepository
            .getCommentsByPost(postId)
            .map(CommentEntity::toModel)
    }

    suspend fun getCommentByIdAndPost(postId: PostId, commentId: CommentId): Comment? {
        return commentRepository.getCommentByIdAndPost(postId, commentId)?.toModel()
    }

}
