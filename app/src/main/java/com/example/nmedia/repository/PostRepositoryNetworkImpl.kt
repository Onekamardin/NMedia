package com.example.nmedia.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.dto.AppDatabase
import com.example.nmedia.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class PostRepositoryNetworkImpl(private val context: Context) : PostRepository {
    private val postDao = AppDatabase.getDatabase(context).postDao()
    private val _posts = MutableLiveData<List<Post>>()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}.type

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken)
            }
    }

    override fun likeById(id: Long) {
        val currentPost = getAll().find { it.id == id }
        val isLiked = currentPost?.likedByMe ?: false

        val request: Request = if (isLiked) {
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .post("".toRequestBody())
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        }

        try {
            val response = client.newCall(request).execute()

            response.use { resp ->
                if (resp.isSuccessful) {
                    val updatedPost = getById(id)
                    _posts.postValue(_posts.value?.map { post ->
                        if (post.id == updatedPost.id) updatedPost else post
                    } ?: listOf(updatedPost))
                } else {
                    throw RuntimeException("Failed to like/unlike post: ${resp.code}")
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error during like/unlike: ${e.message}")
        }
    }



    override fun save(post: Post): Post {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        return gson.fromJson(response.body.string(), Post::class.java)
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }



    override fun getById(postId: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$postId")
            .build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Failed to get post: ${response.code}")
            }
            val body = response.body?.string() ?: throw RuntimeException("Body is null")
            gson.fromJson(body, Post::class.java)
        }
    }


    override fun edit(post: Post): Post {
        TODO("Not yet implemented")
    }


}
