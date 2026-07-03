package com.example.nmedia.api

import com.example.nmedia.dto.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PostApiService {
    @GET("api/slow/posts")
    fun getAll(): Call<List<Post>>

    @POST("api/posts/{id}/likes")
    fun likePost(@Path("id") id: Long): Call<Void>

    @DELETE("api/posts/{id}/likes")
    fun unlikePost(@Path("id") id: Long): Call<Void>

    @POST("api/slow/posts")
    fun savePost(@Body post: Post): Call<Post>

    @DELETE("api/slow/posts/{id}")
    fun removePost(@Path("id") id: Long): Call<Void>

    @PUT("api/slow/posts/{id}")
    fun editPost(@Path("id") id: Long, @Body post: Post): Call<Post>

    @GET("api/slow/posts/{id}")
    fun getPostById(@Path("id") postId: Long): Call<Post>
}

