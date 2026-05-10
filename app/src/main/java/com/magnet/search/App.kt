package com.magnet.search

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.magnet.search.data.local.AppDatabase
import com.magnet.search.domain.MagnetRepository

class App : Application() {

    companion object {
        var instance: App? = null
            private set
        var repository: MagnetRepository? = null
            private set
        var isInitialized = false
            private set
        private const val TAG = "MagnetSearch"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught exception: ${throwable.message}", throwable)
        }
        
        try {
            Log.d(TAG, "Starting initialization...")
            
            val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "magnet_search.db"
            )
                .fallbackToDestructiveMigration()
                .build()

            repository = MagnetRepository(
                favoriteDao = database.favoriteDao(),
                searchHistoryDao = database.searchHistoryDao()
            )
            
            isInitialized = true
            Log.d(TAG, "Initialization complete")
        } catch (e: Exception) {
            Log.e(TAG, "Initialization failed: ${e.message}", e)
            isInitialized = false
        }
    }
}
