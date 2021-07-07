package com.andreformento.myapp.post.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
internal interface PostRepository : CoroutineCrudRepository<PostEntity, UUID> {

    @Query("SELECT * FROM post WHERE ID = :id")
    suspend fun findOne(@Param("id") id: UUID): PostEntity?

    @Modifying
    @Query("update post set title = :title, content = :content where id = :id")
    suspend fun update(
        @Param("id") id: UUID,
        @Param("title") title: String,
        @Param("content") content: String
    ): Int

}
