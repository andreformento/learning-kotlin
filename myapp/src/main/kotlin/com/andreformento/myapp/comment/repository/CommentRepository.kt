package com.andreformento.myapp.comment.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
internal interface CommentRepository : CoroutineCrudRepository<CommentEntity, UUID> {

    @Query("SELECT * FROM comment WHERE post_id = :postId")
    suspend fun getCommentsByPost(@Param("postId") postId: UUID): Flow<CommentEntity>

    @Query("SELECT * FROM comment WHERE id = :commentId AND post_id = :postId")
    suspend fun getCommentByIdAndPost(
        @Param("postId") postId: UUID,
        @Param("commentId") commentId: UUID
    ): CommentEntity?

}
