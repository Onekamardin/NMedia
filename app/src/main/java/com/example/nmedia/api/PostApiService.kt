package com.example.nmedia.api

import com.example.nmedia.dto.*
import retrofit2.http.*

interface PostApiService {
    @GET("api/posts")
    suspend fun getAll(): List<Post>

    @GET("api/authors/{id}")
    suspend fun getAuthor(@Path("id") id: Long): Author

    @GET("api/comments")
    suspend fun getComments(@Query("postId") postId: Long): List<Comment>

    @POST("api/posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long)

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long)

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Post

    @DELETE("api/posts/{id}")
    suspend fun removePost(@Path("id") id: Long)

    @PUT("api/posts/{id}")
    suspend fun editPost(@Path("id") id: Long, @Body post: Post): Post

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") postId: Long): Post
}
