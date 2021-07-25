package com.andreformento.money.comment

import com.andreformento.money.comment.repository.Comments
import com.andreformento.money.post.PostId
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class CommentService(private val comments: Comments) {

    suspend fun getCommentsByPost(postId: PostId): Flow<Comment> {
        return comments.getCommentsByPost(postId)
    }

    suspend fun getCommentByIdAndPost(postId: PostId, commentId: CommentId): Comment? {
        return comments.getCommentByIdAndPost(postId, commentId)
    }

}
