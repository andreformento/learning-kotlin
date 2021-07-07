package com.andreformento.myapp.comment

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("comment")
data class Comment(
    @Id val id: UUID? = null,
    @Column("content") val content: String? = null,
    @Column("post_id") val postId: UUID? = null
)
