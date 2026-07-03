package com.example.nmedia.repository

import com.example.nmedia.dto.Post
import com.example.nmedia.model.PostWithAuthor

interface PostRepository {
    suspend fun getAll(): List<PostWithAuthor>
    suspend fun likeById(id: Long)
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Long)
    suspend fun getById(postId: Long): Post
    suspend fun edit(post: Post): Post
}

