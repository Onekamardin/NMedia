package com.example.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.dto.AppDatabase
import com.example.nmedia.dto.Post
import com.example.nmedia.entity.PostEntity

class PostRepositoryRoomImpl(private val context: Context) : PostRepository {
    private val postDao = AppDatabase.getDatabase(context).postDao()
    private val _posts = MutableLiveData<List<Post>>()

    init {
        loadPostsFromDb()
    }

    private fun loadPostsFromDb() {
        postDao.getAllPosts().observeForever { entities ->
            _posts.value = entities.map { it.toPost() }
        }
    }

    override fun getAll(): LiveData<List<Post>> = _posts

    override fun likeById(id: Long) {
        val post = postDao.getPostById(id) ?: return
        val newLikes = if (post.likedByMe) post.likes - 1 else post.likes + 1
        val newLikedByMe = !post.likedByMe
        postDao.updateLikes(id, newLikes, newLikedByMe)
        loadPostsFromDb()
    }

    override fun repostById(id: Long) {
        val post = postDao.getPostById(id) ?: return
        val newShareCount = post.shareCount + 1
        postDao.updateShares(id, newShareCount)
        loadPostsFromDb()
    }

    override fun removeById(id: Long) {
        postDao.deleteById(id)
        loadPostsFromDb()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            val nextId = (getMaxId() ?: 0L) + 1
            val newPost = post.copy(id = nextId)
            postDao.insert(newPost.toPostEntity())
        } else {
            postDao.update(post.toPostEntity())
        }
        loadPostsFromDb()
    }

    private fun getMaxId(): Long? {
        return postDao.getMaxId()
    }

    override fun getById(postId: Long): Post {
        val entity = postDao.getPostById(postId)
            ?: throw NoSuchElementException("Пост с ID $postId не найден")
        return entity.toPost()
    }

    override fun edit(post: Post): Post {
        postDao.update(post.toPostEntity())
        loadPostsFromDb()
        return post
    }

    private fun PostEntity.toPost(): Post = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likes = likes,
        likedByMe = likedByMe,
        shareCount = shareCount,
        video = video
    )

    private fun Post.toPostEntity(): PostEntity = PostEntity(
        id = id,
        author = author,
        content = content,
        published = published,
        likes = likes,
        likedByMe = likedByMe,
        shareCount = shareCount,
        video = video
    )
}
