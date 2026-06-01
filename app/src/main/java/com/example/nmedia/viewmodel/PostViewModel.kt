package com.example.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.dto.Post
import com.example.nmedia.repository.PostRepository
import com.example.nmedia.repository.PostRepositoryNetworkImpl
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = 0,
)


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryNetworkImpl(application)
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun likeById(id: Long) {
        thread {
            try {
                repository.likeById(id)
                loadPosts()
            } catch (e: IOException) {
                _data.postValue(FeedModel(error = true))
            }
        }
    }

    fun repostById(id: Long) = repository.repostById(id)
    fun removeById(id: Long) = repository.removeById(id)
   // fun saveContent(post: Post) = repository.save(post)
    fun getById(postId: Long): Post = repository.getById(postId)
    fun edit(post: Post): Post = repository.edit(post)


    init {
        loadPosts()
    }

    fun saveContent(post: Post) {
        thread {
            edited.value?.let {
                thread {
                    repository.save(it)
                    _postCreated.postValue(Unit)
                }
            }
            edited.postValue(empty)
        }

    }
    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }



//    fun saveContent(post: Post) {
//        try {
//            val fullPost = if (post.id == 0L) {
//                post.copy(
//                    id = 0L,
//                    author = "User",
//                    published = "Now",
//                    likes = 0,
//                    likedByMe = false,
//                    shareCount = 0
//                )
//            } else post
//
//            repository.save(fullPost)
//        } catch (e: Exception) {
//            Toast.makeText(getApplication(), "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//    fun edit(post: Post) {
//        try {
//            val updatedPost = repository.edit(post)
//            _data.value = _data.value?.map { existingPost ->
//                if (existingPost.id == updatedPost.id) updatedPost else existingPost
//            } ?: listOf(updatedPost)
//        } catch (e: Exception) {
//            Toast.makeText(getApplication(), "Ошибка редактирования: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//    }


}