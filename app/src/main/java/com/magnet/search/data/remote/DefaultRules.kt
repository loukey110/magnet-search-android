package com.magnet.search.data.remote

import com.magnet.search.data.model.SearchRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DefaultRules {
    private val gson = Gson()

    fun getDefaultRules(): List<SearchRule> {
        return listOf(
            SearchRule(
                id = "btdigg",
                name = "BTDigg",
                baseUrl = "https://btdigg.org",
                searchUrl = "https://btdigg.org/search?q={keyword}&p={page}",
                encoding = "UTF-8",
                listSelector = "table.torrents_table tr:not(:first-child)",
                titleSelector = "td.torrent_name a",
                magnetSelector = "td.tt a[href^=magnet]",
                magnetAttr = "href",
                sizeSelector = "td.torrent_name span.attr_val",
                dateSelector = "td.torrent_name span.attr_val",
                priority = 1
            ),
            SearchRule(
                id = "cilibali",
                name = "磁力吧",
                baseUrl = "https://www.cilibali.org",
                searchUrl = "https://www.cilibali.org/search?word={keyword}&page={page}",
                encoding = "UTF-8",
                listSelector = "div.item",
                titleSelector = "a.title",
                magnetSelector = "a[href^=magnet]",
                magnetAttr = "href",
                sizeSelector = "span.size",
                dateSelector = "span.date",
                seederSelector = "span.seeders",
                priority = 2
            ),
            SearchRule(
                id = "btsearch",
                name = "BT搜索",
                baseUrl = "https://btsearch.club",
                searchUrl = "https://btsearch.club/search/{keyword}/{page}",
                encoding = "UTF-8",
                listSelector = "div.search-item",
                titleSelector = "a.title",
                magnetSelector = "a.magnet-link",
                magnetAttr = "href",
                sizeSelector = "span.size",
                dateSelector = "span.date",
                priority = 3
            )
        )
    }

    fun toJson(rules: List<SearchRule>): String {
        return gson.toJson(rules)
    }

    fun fromJson(json: String): List<SearchRule> {
        return try {
            val type = object : TypeToken<List<SearchRule>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
