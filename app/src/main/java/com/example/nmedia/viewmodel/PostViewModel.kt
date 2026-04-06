package com.example.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nmedia.dto.Post
import com.example.nmedia.repository.PostRepository
import com.example.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post()


class PostViewModel : ViewModel() {


    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun repostById(id: Long) = repository.repostById(id)
    fun removeById(id: Long) = repository.removeById(id)

    val edited = MutableLiveData(empty)
    fun saveContent(content: String) {
        edited.value?.let { post ->
            val trimmed = content.trim()
            if (post.content != trimmed) {
                repository.save(post.copy(content = trimmed))
            }
            edited.value = empty
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

}