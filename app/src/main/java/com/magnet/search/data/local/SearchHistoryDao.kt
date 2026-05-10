package com.magnet.search.data.local

import androidx.room.*
import com.magnet.search.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchTime DESC LIMIT 50")
    fun getAllHistory(): Flow<List<SearchHistory>>

    @Query("SELECT DISTINCT keyword FROM search_history ORDER BY searchTime DESC LIMIT 20")
    fun getKeywords(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: SearchHistory)

    @Query("DELETE FROM search_history WHERE keyword = :keyword")
    suspend fun deleteByKeyword(keyword: String)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM search_history WHERE keyword = :keyword)")
    suspend fun exists(keyword: String): Boolean
}
