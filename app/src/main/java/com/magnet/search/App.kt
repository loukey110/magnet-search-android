package com.magnet.search

import android.app.Application
import androidx.room.Room
import com.magnet.search.data.local.AppDatabase
import com.magnet.search.domain.MagnetRepository

class App : Application() {

    companion object {
        lateinit var instance: App private set
        lateinit var repository: MagnetRepository private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "magnet_search.db"
        ).build()

        repository = MagnetRepository(
            favoriteDao = database.favoriteDao(),
            searchHistoryDao = database.searchHistoryDao()
        )
    }
}
