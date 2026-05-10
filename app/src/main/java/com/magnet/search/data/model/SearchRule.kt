package com.magnet.search.data.model

data class SearchRule(
    val id: String,
    val name: String,
    val baseUrl: String,
    val searchUrl: String,
    val searchMethod: String = "GET",
    val searchParam: String = "keyword",
    val pageParam: String = "page",
    val pageSize: Int = 20,
    val encoding: String = "UTF-8",
    val listSelector: String = "",
    val titleSelector: String = "",
    val magnetSelector: String = "",
    val magnetAttr: String = "href",
    val sizeSelector: String = "",
    val dateSelector: String = "",
    val seederSelector: String = "",
    val leecherSelector: String = "",
    val categorySelector: String = "",
    val headers: Map<String, String> = emptyMap(),
    val enabled: Boolean = true,
    val priority: Int = 0
) {
    fun buildSearchUrl(keyword: String, page: Int = 1): String {
        val encodedKeyword = java.net.URLEncoder.encode(keyword, encoding)
        return when {
            searchUrl.contains("{keyword}") && searchUrl.contains("{page}") -> {
                searchUrl.replace("{keyword}", encodedKeyword).replace("{page}", page.toString())
            }
            searchUrl.contains("{keyword}") -> {
                searchUrl.replace("{keyword}", encodedKeyword)
            }
            else -> {
                val sb = StringBuilder(searchUrl)
                if (!searchUrl.contains("?")) sb.append("?") else sb.append("&")
                sb.append("$searchParam=$encodedKeyword")
                if (page > 1) sb.append("&$pageParam=$page")
                sb.toString()
            }
        }
    }
}
