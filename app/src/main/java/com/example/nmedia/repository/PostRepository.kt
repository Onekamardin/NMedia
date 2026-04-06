package com.example.nmedia.repository

import androidx.lifecycle.LiveData
import com.example.nmedia.dto.Post

interface PostRepository {

    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun repostById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
}