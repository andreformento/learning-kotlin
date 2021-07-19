package com.andreformento.myapp.comment.repository

import com.andreformento.myapp.comment.CommentId
import com.andreformento.myapp.post.PostId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface CommentRepository : CoroutineCrudRepository<CommentEntity, CommentId> {

    @Query("SELECT * FROM comment WHERE post_id = :postId")
    suspend fun getCommentsByPost(@Param("postId") postId: PostId): Flow<CommentEntity>

    @Query("SELECT * FROM comment WHERE post_id = :postId AND id = :commentId")
    suspend fun getCommentByIdAndPost(
        @Param("postId") postId: PostId,
        @Param("commentId") commentId: CommentId
    ): CommentEntity?

}
