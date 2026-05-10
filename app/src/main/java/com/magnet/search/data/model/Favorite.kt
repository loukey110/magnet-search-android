package com.magnet.search.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val magnetLink: String,
    val fileSize: String = "",
    val sourceName: String = "",
    val createTime: Long = System.currentTimeMillis()
)
