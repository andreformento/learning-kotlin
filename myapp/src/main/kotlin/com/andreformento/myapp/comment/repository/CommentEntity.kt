package com.andreformento.myapp.comment.repository

import com.andreformento.myapp.comment.Comment
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("comment")
data class CommentEntity(
    @Id val id: UUID,
    @Column("content") val content: String,
    @Column("post_id") val postId: UUID
) {
    fun toModel(): Comment {
        return Comment(this.id, this.content, this.postId)
    }
}
