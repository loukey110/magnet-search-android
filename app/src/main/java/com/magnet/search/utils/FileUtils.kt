package com.magnet.search.utils

import java.text.DecimalFormat

object FileUtils {
    private val df = DecimalFormat("#.##")

    fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${df.format(bytes / 1024.0)} KB"
            bytes < 1024 * 1024 * 1024 -> "${df.format(bytes / (1024.0 * 1024))} MB"
            else -> "${df.format(bytes / (1024.0 * 1024 * 1024))} GB"
        }
    }

    fun parseSize(sizeStr: String): Long {
        if (sizeStr.isEmpty()) return 0
        
        val regex = Regex("([\\d.]+)\\s*(B|KB|MB|GB|TB)", RegexOption.IGNORE_CASE)
        val match = regex.find(sizeStr) ?: return 0
        
        val value = match.groupValues[1].toDoubleOrNull() ?: return 0
        val unit = match.groupValues[2].uppercase()
        
        return when (unit) {
            "B" -> value.toLong()
            "KB" -> (value * 1024).toLong()
            "MB" -> (value * 1024 * 1024).toLong()
            "GB" -> (value * 1024 * 1024 * 1024).toLong()
            "TB" -> (value * 1024 * 1024 * 1024 * 1024).toLong()
            else -> 0
        }
    }
}
