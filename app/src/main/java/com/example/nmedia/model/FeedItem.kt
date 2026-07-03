package com.example.nmedia.model

import com.example.nmedia.dto.*

data class PostWithAuthor(
    val post: Post,
    val author: Author,
    val comments: List<CommentWithAuthor> = emptyList()
)

data class CommentWithAuthor(
    val comment: Comment,
    val author: Author
)
