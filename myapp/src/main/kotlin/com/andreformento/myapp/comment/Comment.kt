package com.andreformento.myapp.comment

import java.util.*

data class Comment(
    val id: UUID,
    val content: String,
    val postId: UUID
)
