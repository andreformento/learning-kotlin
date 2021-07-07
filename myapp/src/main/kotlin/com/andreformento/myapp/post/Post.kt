package com.andreformento.myapp.post

import java.util.*

typealias PostId = UUID

fun String.toPostId(): PostId {
    return PostId.fromString(this)
}

data class Post(
    val id: PostId,
    val title: String,
    val content: String
)
