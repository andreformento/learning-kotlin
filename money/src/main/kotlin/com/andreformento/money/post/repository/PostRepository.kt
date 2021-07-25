package com.andreformento.money.post.repository

import com.andreformento.money.post.PostId
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface PostRepository : CoroutineCrudRepository<PostEntity, PostId> {

    @Query("SELECT * FROM post WHERE ID = :id")
    suspend fun findOne(@Param("id") id: PostId): PostEntity?

    @Modifying
    @Query("update post set title = :title, content = :content where id = :id")
    suspend fun update(
        @Param("id") id: PostId,
        @Param("title") title: String,
        @Param("content") content: String
    ): Int

}
