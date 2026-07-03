package com.example.nmedia.model


data class FeedModel(
    val posts: List<PostWithAuthor> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
)
