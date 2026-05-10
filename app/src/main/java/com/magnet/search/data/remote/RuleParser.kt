package com.magnet.search.data.remote

import com.magnet.search.data.model.MagnetItem
import com.magnet.search.data.model.SearchRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import kotlin.math.min

class RuleParser {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun search(rule: SearchRule, keyword: String, page: Int = 1): Result<List<MagnetItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = rule.buildSearchUrl(keyword, page)
                val request = buildRequest(url, rule)
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }

                val html = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                val items = parseHtml(html, rule)
                Result.success(items)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun buildRequest(url: String, rule: SearchRule): Request {
        val builder = Request.Builder().url(url)
        rule.headers.forEach { (key, value) ->
            builder.addHeader(key, value)
        }
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        builder.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        builder.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
        return builder.build()
    }

    private fun parseHtml(html: String, rule: SearchRule): List<MagnetItem> {
        if (rule.listSelector.isEmpty()) return emptyList()

        val doc = Jsoup.parse(html)
        val items = mutableListOf<MagnetItem>()
        val elements = doc.select(rule.listSelector)

        for (element in elements) {
            try {
                val title = extractText(element, rule.titleSelector)
                var magnetLink = extractAttr(element, rule.magnetSelector, rule.magnetAttr)
                
                if (title.isEmpty() || magnetLink.isEmpty()) continue
                
                if (!magnetLink.startsWith("magnet:")) {
                    if (magnetLink.contains("magnet:?")) {
                        magnetLink = "magnet:?" + magnetLink.substringAfter("magnet:?")
                    } else {
                        continue
                    }
                }

                val item = MagnetItem(
                    title = title,
                    magnetLink = magnetLink,
                    fileSize = extractText(element, rule.sizeSelector),
                    uploadDate = extractText(element, rule.dateSelector),
                    seeders = extractNumber(element, rule.seederSelector),
                    leechers = extractNumber(element, rule.leecherSelector),
                    sourceName = rule.name
                )
                items.add(item)
            } catch (e: Exception) {
                continue
            }
        }

        return items
    }

    private fun extractText(element: org.jsoup.nodes.Element, selector: String): String {
        if (selector.isEmpty()) return ""
        return try {
            val selected = if (selector.startsWith(">")) {
                element.selectFirst(selector.substring(1))
            } else {
                element.selectFirst(selector)
            }
            selected?.text()?.trim() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractAttr(element: org.jsoup.nodes.Element, selector: String, attr: String): String {
        if (selector.isEmpty()) return ""
        return try {
            val selected = if (selector.startsWith(">")) {
                element.selectFirst(selector.substring(1))
            } else {
                element.selectFirst(selector)
            }
            when (attr) {
                "href" -> selected?.attr("href")?.trim() ?: ""
                "src" -> selected?.attr("src")?.trim() ?: ""
                "data-magnet" -> selected?.attr("data-magnet")?.trim() ?: ""
                else -> selected?.attr(attr)?.trim() ?: ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractNumber(element: org.jsoup.nodes.Element, selector: String): Int {
        val text = extractText(element, selector)
        return text.filter { it.isDigit() }.toIntOrNull() ?: 0
    }
}
