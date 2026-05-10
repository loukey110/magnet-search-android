package com.magnet.search

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this).apply {
            text = "磁力搜索 - 测试版本"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
        
        setContentView(textView)
    }
}
