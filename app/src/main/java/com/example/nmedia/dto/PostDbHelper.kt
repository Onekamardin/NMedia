package com.example.nmedia.dto

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PostDbHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "nmedia.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_POSTS = "posts"

        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_SHARE_COUNT = "shareCount"
        const val COLUMN_VIDEO = "video"
    }

    private val SQL_CREATE_TABLE = """
        CREATE TABLE $TABLE_POSTS (
            $COLUMN_ID INTEGER PRIMARY KEY,
            $COLUMN_AUTHOR TEXT NOT NULL,
            $COLUMN_CONTENT TEXT NOT NULL,
            $COLUMN_PUBLISHED TEXT NOT NULL,
            $COLUMN_LIKES INTEGER DEFAULT 0,
            $COLUMN_LIKED_BY_ME INTEGER DEFAULT 0,
            $COLUMN_SHARE_COUNT INTEGER DEFAULT 0,
            $COLUMN_VIDEO TEXT
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POSTS")
        onCreate(db)
    }
}
