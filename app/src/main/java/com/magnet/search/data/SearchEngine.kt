package com.magnet.search.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

object SearchEngine {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun search(keyword: String): List<MagnetItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<MagnetItem>()
        
        try {
            val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
            
            val sources = listOf(
                "https://btdigg.org/search?q=$encodedKeyword",
                "https://www.cilibali.org/search?word=$encodedKeyword"
            )
            
            for (url in sources) {
                try {
                    val items = searchFromUrl(url)
                    results.addAll(items)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        results
    }
    
    private fun searchFromUrl(url: String): List<MagnetItem> {
        val items = mutableListOf<MagnetItem>()
        
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "text/html,application/xhtml+xml")
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return items
            
            val html = response.body?.string() ?: return items
            val doc = Jsoup.parse(html)
            
            val links = doc.select("a[href^=magnet:]")
            for (link in links) {
                val magnet = link.attr("href")
                val title = link.text().ifEmpty { 
                    link.parent()?.text() ?: "Unknown"
                }
                
                if (magnet.isNotEmpty() && title.isNotEmpty()) {
                    items.add(MagnetItem(
                        title = title,
                        magnetLink = magnet,
                        source = url.substringAfter("://").substringBefore("/")
                    ))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        items
    }
}
