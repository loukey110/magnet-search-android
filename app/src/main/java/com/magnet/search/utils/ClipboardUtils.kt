package com.magnet.search.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

object ClipboardUtils {
    fun copyMagnetLink(context: Context, magnetLink: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("磁力链接", magnetLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "已复制磁力链接", Toast.LENGTH_SHORT).show()
    }
}
