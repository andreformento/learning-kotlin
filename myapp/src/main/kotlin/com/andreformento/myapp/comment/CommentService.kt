package com.andreformento.myapp.comment

import com.andreformento.myapp.comment.repository.Comments
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentService(private val comments: Comments) {

    suspend fun getCommentsByPost(postId: UUID): Flow<Comment> {
        return comments.getCommentsByPost(postId)
    }

    suspend fun getCommentByIdAndPost(postId: UUID, commentId: UUID): Comment? {
        return comments.getCommentByIdAndPost(postId, commentId)
    }

}
