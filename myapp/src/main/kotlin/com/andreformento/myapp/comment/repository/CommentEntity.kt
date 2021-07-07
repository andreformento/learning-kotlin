package com.andreformento.myapp.comment.repository

import com.andreformento.myapp.comment.Comment
import com.andreformento.myapp.comment.CommentId
import com.andreformento.myapp.post.PostId
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
