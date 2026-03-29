package com.example.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nmedia.repository.PostRepository
import com.example.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel: ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    fun like() = repository.like()
    fun repost() = repository.repost()
}