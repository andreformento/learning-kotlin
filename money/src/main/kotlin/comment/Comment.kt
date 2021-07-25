package com.andreformento.money.comment

import com.andreformento.money.post.PostId
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
