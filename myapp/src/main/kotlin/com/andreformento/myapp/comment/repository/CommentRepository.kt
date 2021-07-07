package com.andreformento.myapp.comment.repository

import com.andreformento.myapp.comment.Comment
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.*

@Repository
private interface CommentRepository : Comments, CoroutineCrudRepository<Comment, UUID> {

    @Query("SELECT * FROM comment WHERE post_id = :postId")
    override suspend fun getCommentsByPost(@Param("postId") postId: UUID): Flow<Comment>

    @Query("SELECT * FROM comment WHERE id = :commentId AND post_id = :postId")
    override suspend fun getCommentByIdAndPost(@Param("postId") postId: UUID, @Param("commentId") commentId: UUID): Comment?

}
