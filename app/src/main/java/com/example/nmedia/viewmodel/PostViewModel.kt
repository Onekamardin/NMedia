package com.example.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nmedia.dto.Post
import com.example.nmedia.repository.PostRepository
import com.example.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post()


class PostViewModel : ViewModel() {


    private val _isEditing = MutableLiveData<Boolean>(false)
    val isEditing: LiveData<Boolean> = _isEditing

    private val _editedPost = MutableLiveData<Post?>(null)
    val editedPost: LiveData<Post?> = _editedPost
    fun startEditing() {
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
        _editedPost.value = null
    }


    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun repostById(id: Long) = repository.repostById(id)
    fun removeById(id: Long) = repository.removeById(id)

    val edited = MutableLiveData(empty)
    fun saveContent(content: String) {
        val currentPost = _editedPost.value

        if (currentPost != null) {

            val updatedPost = currentPost.copy(content = content.trim())
            repository.save(updatedPost)
        } else {
            val newPost = Post()
            repository.save(newPost)
        }

        cancelEditing()
    }

    fun edit(post: Post) {
        _editedPost.value = post
        _isEditing.value = true
    }

}