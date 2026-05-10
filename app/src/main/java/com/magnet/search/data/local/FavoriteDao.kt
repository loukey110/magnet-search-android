package com.magnet.search.data.local

import androidx.room.*
import com.magnet.search.data.model.Favorite
import com.magnet.search.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY createTime DESC")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE magnetLink = :magnetLink LIMIT 1")
    suspend fun findByMagnetLink(magnetLink: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE magnetLink = :magnetLink")
    suspend fun deleteByMagnetLink(magnetLink: String)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM favorites WHERE magnetLink = :magnetLink")
    suspend fun isFavorite(magnetLink: String): Int
}
