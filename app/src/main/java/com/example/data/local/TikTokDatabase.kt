package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [VideoEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TikTokDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: TikTokDatabase? = null

        fun getDatabase(context: Context): TikTokDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TikTokDatabase::class.java,
                    "tiktok_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
