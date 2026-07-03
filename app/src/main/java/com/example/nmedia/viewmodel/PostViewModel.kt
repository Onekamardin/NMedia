package com.example.nmedia.viewmodel



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nmedia.dto.Post
import com.example.nmedia.exception.NetworkException
import com.example.nmedia.repository.PostRepository
import ru.netology.nmedia.model.FeedModel
import kotlin.concurrent.thread


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = 0,
    authorAvatar = ""
)


class PostViewModel(private val repository: PostRepository) : ViewModel() {
    private val _postCreated = MutableLiveData<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    private val _errorEvent = MutableLiveData<String>()
    val errorEvent: LiveData<String> = _errorEvent

    private var errorCallback: ((String) -> Unit)? = null

    fun setErrorCallback(callback: (String) -> Unit) {
        errorCallback = callback
    }
    private val _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel> = _data

    private var currentPosts: List<Post> = emptyList()

    fun loadPosts() {
        _data.value = _data.value?.copy(loading = true, error = false)
        thread {
            try {
                currentPosts = repository.getAll()
                _data.postValue(FeedModel(
                    posts = currentPosts,
                    loading = false,
                    empty = currentPosts.isEmpty()
                ))
            } catch (e: Exception) {
                _data.postValue(_data.value?.copy(
                    loading = false,
                    error = true
                )!!)
            }
        }
    }

    fun retryLoadPosts() {
        loadPosts()
    }

    fun likeById(id: Long) {
        thread {
            try {
                repository.likeById(id)
                loadPosts()
            } catch (e: Exception) {
                errorCallback?.invoke("Ошибка при лайке: ${e.message}")
            }
        }
    }

    fun removeById(id: Long) {
        thread {
            try {
                repository.removeById(id)
                loadPosts()
            } catch (e: Exception) {
                errorCallback?.invoke("Ошибка при удалении: ${e.message}")
            }
        }
    }

    fun saveContent(post: Post) {
        thread {
            try {
                repository.save(post)
                _postCreated.postValue(Unit)
            } catch (e: Exception) {
                _errorEvent.postValue("Ошибка при сохранении: ${e.message}")
            }
        }
    }


    fun edit(post: Post) {
        thread {
            try {
                repository.edit(post)
                loadPosts()
            } catch (e: Exception) {
                errorCallback?.invoke("Ошибка при редактировании: ${e.message}")
            }
        }
    }

    fun getById(postId: Long): Post {
        return try {
            repository.getById(postId)
        } catch (e: Exception) {
            throw NetworkException("Failed to get post by ID: ${e.message}")
        }
    }






}


