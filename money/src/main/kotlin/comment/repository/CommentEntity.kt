package com.andreformento.money.comment.repository

import com.andreformento.money.comment.Comment
import com.andreformento.money.comment.CommentId
import com.andreformento.money.post.PostId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("comment")
data class CommentEntity(
    @Id val id: CommentId,
    @Column("content") val content: String,
    @Column("post_id") val postId: PostId
) {
    fun toModel(): Comment {
        return Comment(this.id, this.content, this.postId)
    }
}
