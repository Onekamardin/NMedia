package com.example.nmedia.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.api.RetrofitClient
import com.example.nmedia.dto.AppDatabase
import com.example.nmedia.dto.Post
import com.example.nmedia.exception.HttpException
import com.example.nmedia.exception.NetworkException

class PostRepositoryRetrofitImpl(private val context: Context) : PostRepository {
    private val postDao = AppDatabase.getDatabase(context).postDao()
    private val _posts = MutableLiveData<List<Post>>()

    private val apiService = RetrofitClient.postApiService

    override fun getAll(): List<Post> {
        return try {
            val response = apiService.getAll().execute()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw NetworkException("Failed to load posts: ${e.message}")
        }
    }

    override fun likeById(id: Long) {
        try {
            val likeResponse = apiService.likePost(id).execute()
            if (!likeResponse.isSuccessful) throw HttpException(likeResponse)
        } catch (e: Exception) {
            throw NetworkException("Error during like: ${e.message}")
        }
    }


    override fun save(post: Post): Post {
        return try {
            val response = apiService.savePost(post).execute()
            if (response.isSuccessful) {
                response.body() ?: throw NetworkException("Empty response")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw NetworkException("Failed to save post: ${e.message}")
        }
    }

    override fun removeById(id: Long) {
        try {
            val response = apiService.removePost(id).execute()
            if (!response.isSuccessful) throw HttpException(response)
        } catch (e: Exception) {
            throw NetworkException("Failed to remove post: ${e.message}")
        }
    }

    override fun getById(postId: Long): Post {
        return try {
            val response = apiService.getPostById(postId).execute()
            if (response.isSuccessful) {
                response.body() ?: throw NetworkException("Post not found")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw NetworkException("Failed to get post: ${e.message}")
        }
    }

    override fun edit(post: Post): Post {
        return try {
            val response = apiService.editPost(post.id, post).execute()
            if (response.isSuccessful) {
                response.body() ?: throw NetworkException("Failed to edit post")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw NetworkException("Failed to edit post: ${e.message}")
        }
    }


}
