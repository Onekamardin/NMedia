package com.example.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.nmedia.dto.Post
import com.example.nmedia.model.FeedModel
import com.example.nmedia.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel> = _data

    private val _errorText = MutableLiveData<String?>()
    val errorText: LiveData<String?> = _errorText


    fun getPostById(targetId: Long): Post? {
        return _data.value?.posts
            ?.find { it.post.id == targetId }
            ?.post
    }



    fun loadPosts() = viewModelScope.launch {
        _data.value = _data.value?.copy(loading = true, error = false)
        try {
            val postsWithAuthors = repository.getAll()
            _data.postValue(
                FeedModel(
                    posts = postsWithAuthors,
                    loading = false,
                    empty = postsWithAuthors.isEmpty()
                )
            )
            // Если всё ок — сбрасываем ошибку
            _errorText.value = null
        } catch (e: Exception) {
            _errorText.value = e.message ?: "Произошла ошибка загрузки"

            _data.postValue(
                _data.value?.copy(
                    loading = false,
                    error = true
                ) ?: FeedModel(loading = false, error = true)
            )
        }
    }

    fun retryLoadPosts() {
        loadPosts()
    }


    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
            loadPosts()
        } catch (e: Exception) {
            _errorText.value = e.message
        }
    }

    fun saveContent(post: Post) = viewModelScope.launch {
        try {
            repository.save(post)
            loadPosts()
        } catch (e: Exception) {
            _errorText.value = e.message
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
            loadPosts()
        } catch (e: Exception) {
            _errorText.value = e.message
        }
    }

    fun edit(post: Post) = viewModelScope.launch {
        try {
            repository.edit(post)
            loadPosts()
        } catch (e: Exception) {
            _errorText.value = e.message
        }
    }


}
