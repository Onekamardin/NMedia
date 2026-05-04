package com.example.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.dto.Post
import com.example.nmedia.repository.PostRepository
import com.example.nmedia.repository.PostRepositoryRoomImpl

private val empty = Post()


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl(application)
    val data = repository.getAll()

    fun likeById(id: Long) = repository.likeById(id)
    fun repostById(id: Long) = repository.repostById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun saveContent(post: Post) = repository.save(post)
    fun getById(postId: Long): Post = repository.getById(postId)
    fun edit(post: Post): Post = repository.edit(post)


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