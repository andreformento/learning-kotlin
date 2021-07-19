package com.andreformento.myapp.comment

import com.andreformento.myapp.post.PostId
import java.util.*

typealias CommentId = UUID

fun String.toCommentId(): CommentId {
    return CommentId.fromString(this)
}

data class Comment(
    val id: CommentId,
    val content: String,
    val postId: PostId
)
