package com.magnet.search.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.magnet.search.data.model.Favorite
import com.magnet.search.data.model.SearchHistory

@Database(
    entities = [Favorite::class, SearchHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
