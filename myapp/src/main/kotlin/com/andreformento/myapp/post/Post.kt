package com.andreformento.myapp.post

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("post")
data class Post(
    @Id val id: UUID? = null,
    @Column("title") val title: String? = null,
    @Column("content") val content: String? = null
)
