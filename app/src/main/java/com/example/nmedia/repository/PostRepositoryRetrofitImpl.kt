package com.example.nmedia.repository

import android.content.Context
import com.example.nmedia.api.RetrofitClient
import com.example.nmedia.dto.*
import com.example.nmedia.exception.NetworkException
import com.example.nmedia.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepositoryRetrofitImpl(private val context: Context) : PostRepository {

    private val postDao = AppDatabase.getDatabase(context).postDao()
    private val apiService = RetrofitClient.postApiService

    override suspend fun getAll(): List<PostWithAuthor> = withContext(Dispatchers.IO) {
        return@withContext try {
            val posts = apiService.getAll()

            val authorsMap = mutableMapOf<Long, Author>()
            for (post in posts) {
                val author = apiService.getAuthor(post.authorId)
                authorsMap[post.authorId] = author
            }

            val result = mutableListOf<PostWithAuthor>()
            for (post in posts) {
                val author = authorsMap[post.authorId]
                    ?: throw Exception("Author not found for post ${post.id}")

                val comments = apiService.getComments(post.id)

                val commentAuthorsMap = mutableMapOf<Long, Author>()
                for (c in comments) {
                    val ca = apiService.getAuthor(c.authorId)
                    commentAuthorsMap[c.authorId] = ca
                }

                val commentsWithAuthors = comments.mapNotNull { comment ->
                    commentAuthorsMap[comment.authorId]?.let { a ->
                        CommentWithAuthor(comment, a)
                    }
                }

                result.add(PostWithAuthor(post, author, commentsWithAuthors))
            }
            result
        } catch (e: Exception) {
            throw NetworkException("Failed to load posts: ${e.message}")
        }
    }


    override suspend fun likeById(id: Long) {
        try {
            apiService.likePost(id)
        } catch (e: Exception) {
            throw NetworkException("Like failed: ${e.message}")
        }
    }

    override suspend fun save(post: Post): Post {
        return apiService.savePost(post)
    }

    override suspend fun removeById(id: Long) {
        apiService.removePost(id)
    }

    override suspend fun getById(postId: Long): Post {
        return apiService.getPostById(postId)
    }

    override suspend fun edit(post: Post): Post {
        return apiService.editPost(post.id, post)
    }
}
