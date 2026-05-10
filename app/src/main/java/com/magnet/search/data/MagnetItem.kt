package com.magnet.search.data

data class MagnetItem(
    val title: String,
    val magnetLink: String,
    val size: String = "",
    val date: String = "",
    val source: String = ""
)
