package com.example.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: PostEntity)

    @Update
    fun update(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    fun deleteById(postId: Long)

    @Query("UPDATE posts SET likes = :likes, likedByMe = :likedByMe WHERE id = :postId")
    fun updateLikes(postId: Long, likes: Int, likedByMe: Boolean)

    @Query("UPDATE posts SET shareCount = :shareCount WHERE id = :postId")
    fun updateShares(postId: Long, shareCount: Int)
}

