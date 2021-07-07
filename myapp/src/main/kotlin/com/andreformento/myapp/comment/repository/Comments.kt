package com.andreformento.myapp.comment.repository

import com.andreformento.myapp.comment.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class Comments internal constructor(private val commentRepository: CommentRepository) {

    suspend fun getCommentsByPost(postId: UUID): Flow<Comment> {
        return commentRepository
            .getCommentsByPost(postId)
            .map(CommentEntity::toModel)
    }

    suspend fun getCommentByIdAndPost(postId: UUID, commentId: UUID): Comment? {
        return commentRepository.getCommentByIdAndPost(postId,commentId)?.toModel()
    }

}
