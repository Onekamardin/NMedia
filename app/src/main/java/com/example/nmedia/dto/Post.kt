package com.example.nmedia.dto

data class Post(
    val id: Int,
    val author: String,
    val published: String,
    val content: String,
    var likes: Int = 0,
    val likedByMe: Boolean = false,
    val shareCount: Int = 0
)