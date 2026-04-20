package com.example.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private var posts = listOf(
        Post(
            1,
            "Нетология. Университет интернет проф...",
            "21 мая в 18:36",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен.",
            shareCount = 999,
            likes = 1000,
            likedByMe = true
        ),
        Post(
            2,
            "Нетология. Университет интернет проф...",
            "21 мая в 18:36",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен.",
            shareCount = 500,
            likes = 1000,
            likedByMe = true
        ),
        Post(
            id = 3,
            author = "Автор",
            published = "Сегодня",
            content = "Пост с видео!",
            video = "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb"
        )
    )
    private var nextId = posts.first().id + 1


    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
    }

    override fun repostById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(shareCount = post.shareCount + 1)
            } else {
                post
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(post.copy(id = nextId++, author = "Me", published = "Now")) + posts
        } else {
            posts = posts.map {
                if (it.id == post.id) {
                    it.copy(content = post.content)
                } else it
            }
        }
        data.value = posts
    }

    override fun getById(postId: Long): Post {
        return posts.firstOrNull { it.id == postId }
            ?: throw NoSuchElementException("Пост с ID $postId не найден")
    }

}