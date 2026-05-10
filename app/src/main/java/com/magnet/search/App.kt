package com.magnet.search

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.magnet.search.data.local.AppDatabase
import com.magnet.search.domain.MagnetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {

    companion object {
        lateinit var instance: App private set
        lateinit var repository: MagnetRepository private set
        private const val TAG = "MagnetSearch"
    }

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught exception in thread: ${thread.name}", throwable)
        }
        
        try {
            Log.d(TAG, "Initializing application...")
            
            val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "magnet_search.db"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()

            repository = MagnetRepository(
                favoriteDao = database.favoriteDao(),
                searchHistoryDao = database.searchHistoryDao()
            )
            
            Log.d(TAG, "Application initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize application", e)
        }
    }
}
