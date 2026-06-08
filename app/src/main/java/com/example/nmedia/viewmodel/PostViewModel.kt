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


    fun removeById(id: Long) = repository.removeById(id)
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
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

}