package com.example.nmedia.viewmodel

import android.util.Log
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

    private fun cancelEditing() {
        _isEditing.value = false
        _editedPost.value = null
    }


    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun repostById(id: Long) = repository.repostById(id)
    fun removeById(id: Long) = repository.removeById(id)

    val edited = MutableLiveData(empty)

    fun saveContent(content: String, postId: Long = 0L) {
        if (postId == 0L) {
            val newPost = Post(content = content.trim())
            repository.save(newPost)
        } else {
            try {
                val existingPost = repository.getById(postId)
                val updatedPost = existingPost.copy(content = content.trim())
                repository.save(updatedPost)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Пост с ID $postId не найден", e)
            }
        }
        cancelEditing()
    }


    fun edit(post: Post) {
        _editedPost.value = post
        _isEditing.value = true
    }

}