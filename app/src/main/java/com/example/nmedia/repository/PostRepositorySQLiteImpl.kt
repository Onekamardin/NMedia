package com.example.nmedia.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.dto.Post
import com.example.nmedia.dto.PostDbHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PostRepositorySQLiteImpl(private val context: Context) : PostRepository {

    private val dbHelper = PostDbHelper(context)
    private val _posts = MutableLiveData<List<Post>>()

    init {
        loadPostsFromDb()
    }

    private val gson = Gson()

    private var posts = readPosts()
        set(value) {
            field = value
            sync()
        }


    private var nextId = (posts.maxByOrNull { it.id }?.id ?: 0L) + 1L

    private val data = MutableLiveData(posts)

    //override fun getAll(): LiveData<List<Post>> = data
    override fun getAll(): LiveData<List<Post>> = _posts

    private fun loadPostsFromDb() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            PostDbHelper.TABLE_POSTS,
            null,
            null,
            null,
            null,
            null,
            "${PostDbHelper.COLUMN_ID} DESC"
        )

        val postsList = mutableListOf<Post>()
        with(cursor) {
            while (moveToNext()) {
                val post = Post(
                    id = getLong(getColumnIndexOrThrow(PostDbHelper.COLUMN_ID)),
                    author = getString(getColumnIndexOrThrow(PostDbHelper.COLUMN_AUTHOR)),
                    content = getString(getColumnIndexOrThrow(PostDbHelper.COLUMN_CONTENT)),
                    published = getString(getColumnIndexOrThrow(PostDbHelper.COLUMN_PUBLISHED)),
                    likes = getInt(getColumnIndexOrThrow(PostDbHelper.COLUMN_LIKES)),
                    likedByMe = getInt(getColumnIndexOrThrow(PostDbHelper.COLUMN_LIKED_BY_ME)) == 1,
                    shareCount = getInt(getColumnIndexOrThrow(PostDbHelper.COLUMN_SHARE_COUNT)),
                    video = getString(getColumnIndexOrThrow(PostDbHelper.COLUMN_VIDEO))
                )
                postsList.add(post)
            }
        }
        cursor.close()
        _posts.value = postsList
    }

    override fun likeById(id: Long) {
        val db = dbHelper.writableDatabase
        val post = getById(id)
        val newLikes = if (post.likedByMe) post.likes - 1 else post.likes + 1
        val newLikedByMe = !post.likedByMe

        val values = ContentValues().apply {
            put(PostDbHelper.COLUMN_LIKES, newLikes)
            put(PostDbHelper.COLUMN_LIKED_BY_ME, if (newLikedByMe) 1 else 0)
        }

        db.update(
            PostDbHelper.TABLE_POSTS,
            values,
            "${PostDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        loadPostsFromDb()
    }

    override fun repostById(id: Long) {
        val db = dbHelper.writableDatabase
        val post = getById(id)
        val newShareCount = post.shareCount + 1

        val values = ContentValues().apply {
            put(PostDbHelper.COLUMN_SHARE_COUNT, newShareCount)
        }

        db.update(
            PostDbHelper.TABLE_POSTS,
            values,
            "${PostDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        loadPostsFromDb()
    }

    override fun removeById(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(
            PostDbHelper.TABLE_POSTS,
            "${PostDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        loadPostsFromDb()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            val nextId = (getMaxId() ?: 0L) + 1
            val newPost = post.copy(id = nextId)
            insertPost(newPost)
        } else {
            updatePost(post)
        }
    }

    private fun getMaxId(): Long? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT MAX(${PostDbHelper.COLUMN_ID}) FROM ${PostDbHelper.TABLE_POSTS}",
            null
        )
        return if (cursor.moveToFirst()) {
            cursor.getLong(0)
        } else {
            null
        }.also { cursor.close() }
    }

    private fun insertPost(post: Post) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(PostDbHelper.COLUMN_ID, post.id)
            put(PostDbHelper.COLUMN_AUTHOR, post.author)
            put(PostDbHelper.COLUMN_CONTENT, post.content)
            put(PostDbHelper.COLUMN_PUBLISHED, post.published)
            put(PostDbHelper.COLUMN_LIKES, post.likes)
            put(PostDbHelper.COLUMN_LIKED_BY_ME, if (post.likedByMe) 1 else 0)
            put(PostDbHelper.COLUMN_SHARE_COUNT, post.shareCount)
            post.video?.let { put(PostDbHelper.COLUMN_VIDEO, it) }
        }
        db.insert(PostDbHelper.TABLE_POSTS, null, values)
        loadPostsFromDb()
    }

    private fun updatePost(post: Post) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(PostDbHelper.COLUMN_AUTHOR, post.author)
            put(PostDbHelper.COLUMN_CONTENT, post.content)
            put(PostDbHelper.COLUMN_PUBLISHED, post.published)
            put(PostDbHelper.COLUMN_LIKES, post.likes)
            put(PostDbHelper.COLUMN_LIKED_BY_ME, if (post.likedByMe) 1 else 0)
            put(PostDbHelper.COLUMN_SHARE_COUNT, post.shareCount)
            post.video?.let { put(PostDbHelper.COLUMN_VIDEO, it) }
                ?: putNull(PostDbHelper.COLUMN_VIDEO)
        }

        db.update(
            PostDbHelper.TABLE_POSTS,
            values,
            "${PostDbHelper.COLUMN_ID} = ?",
            arrayOf(post.id.toString())
        )
        loadPostsFromDb()
    }

    override fun getById(postId: Long): Post {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            PostDbHelper.TABLE_POSTS,
            null,
            "${PostDbHelper.COLUMN_ID} = ?",
            arrayOf(postId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            Post(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_ID)),
                author = cursor.getString(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_AUTHOR)),
                content = cursor.getString(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_CONTENT)),
                published = cursor.getString(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_PUBLISHED)),
                likes = cursor.getInt(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_LIKES)),
                likedByMe = cursor.getInt(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_LIKED_BY_ME)) == 1,
                shareCount = cursor.getInt(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_SHARE_COUNT)),
                video = cursor.getString(cursor.getColumnIndexOrThrow(PostDbHelper.COLUMN_VIDEO))
            )
        } else {
            throw NoSuchElementException("Пост с ID $postId не найден")
        }.also { cursor.close() }
    }

    override fun edit(post: Post): Post {
        updatePost(post)
        return post
    }


    private fun readPosts(): List<Post> {
        val file = context.filesDir.resolve(FILE_NAME)

        return if (file.exists() && file.length() > 0) {
            try {
                file.reader().buffered().use { reader ->
                    gson.fromJson<List<Post>>(reader, postType)?.let { jsonList ->
                        jsonList.ifEmpty { emptyList() }
                    } ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "Ошибка чтения или парсинга posts.json", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }


    private fun sync() {
        val file = context.filesDir.resolve(FILE_NAME)
        file.writer().buffered().use {
            it.write(gson.toJson(posts))
        }
    }

    private companion object {
        const val FILE_NAME = "posts.json"
        val postType: Type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    }

}

