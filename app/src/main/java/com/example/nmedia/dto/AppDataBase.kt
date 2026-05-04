package com.example.nmedia.dto

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nmedia.dao.PostDao

abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nmedia.db"
                )
                    .allowMainThreadQueries() // Обязательно!
                    .build()
                    .also { INSTANCE = it }
                instance
            }
        }
    }
}


