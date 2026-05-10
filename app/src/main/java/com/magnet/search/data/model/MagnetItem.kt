package com.magnet.search.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "magnet_items")
data class MagnetItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val magnetLink: String,
    val fileSize: String = "",
    val fileCount: Int = 0,
    val seeders: Int = 0,
    val leechers: Int = 0,
    val uploadDate: String = "",
    val sourceName: String = "",
    val category: String = "",
    val hotValue: Int = 0,
    val createTime: Long = System.currentTimeMillis()
) {
    fun getShortTitle(): String {
        return if (title.length > 50) title.substring(0, 50) + "..." else title
    }
}
