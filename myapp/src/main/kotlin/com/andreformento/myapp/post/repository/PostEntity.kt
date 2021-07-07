package com.andreformento.myapp.post.repository

import com.andreformento.myapp.post.Post
import com.andreformento.myapp.post.PostCreation
import com.andreformento.myapp.post.PostId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("post")
data class PostEntity(
    @Id val id: PostId? = null,
    @Column("title") val title: String,
    @Column("content") val content: String
) {
    constructor(postCreation: PostCreation) : this(title = postCreation.title, content = postCreation.content)

    fun toModel(): Post {
        return Post(this.id!!, this.title, this.content)
    }
}
