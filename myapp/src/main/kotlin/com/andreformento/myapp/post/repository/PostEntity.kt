package com.andreformento.myapp.post.repository

import com.andreformento.myapp.post.Post
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("post")
data class PostEntity(
    @Id val id: UUID,
    @Column("title") val title: String,
    @Column("content") val content: String
) {
    constructor(post: Post) : this(post.id, post.title, post.content)

    fun toModel(): Post {
        return Post(this.id, this.title, this.content)
    }
}
