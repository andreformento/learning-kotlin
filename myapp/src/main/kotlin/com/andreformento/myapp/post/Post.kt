package com.andreformento.myapp.post

import java.util.*

data class Post(
    val id: UUID,
    val title: String,
    val content: String
)
