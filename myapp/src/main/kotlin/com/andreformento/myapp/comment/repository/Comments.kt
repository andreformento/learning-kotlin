package com.andreformento.myapp.comment.repository

import com.andreformento.myapp.comment.Comment
import kotlinx.coroutines.flow.Flow
import java.util.*

interface Comments {

    suspend fun getCommentsByPost(postId: UUID): Flow<Comment>

    suspend fun getCommentByIdAndPost(postId: UUID, commentId: UUID): Comment?

}
