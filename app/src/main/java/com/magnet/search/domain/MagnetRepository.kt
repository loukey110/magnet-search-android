package com.magnet.search.domain

import com.magnet.search.data.local.FavoriteDao
import com.magnet.search.data.local.SearchHistoryDao
import com.magnet.search.data.model.Favorite
import com.magnet.search.data.model.MagnetItem
import com.magnet.search.data.model.SearchHistory
import com.magnet.search.data.model.SearchRule
import com.magnet.search.data.remote.DefaultRules
import com.magnet.search.data.remote.RuleParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MagnetRepository(
    private val favoriteDao: FavoriteDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val ruleParser: RuleParser = RuleParser()
) {
    private var rules: List<SearchRule> = DefaultRules.getDefaultRules()

    fun getRules(): List<SearchRule> = rules

    fun updateRules(newRules: List<SearchRule>) {
        rules = newRules
    }

    suspend fun search(keyword: String, page: Int = 1): Result<List<MagnetItem>> {
        return withContext(Dispatchers.IO) {
            if (keyword.isBlank()) {
                return@withContext Result.failure(Exception("搜索关键词不能为空"))
            }

            val enabledRules = rules.filter { it.enabled }
            if (enabledRules.isEmpty()) {
                return@withContext Result.failure(Exception("没有可用的搜索源"))
            }

            val results = mutableListOf<MagnetItem>()
            val deferredResults = enabledRules.map { rule ->
                async {
                    ruleParser.search(rule, keyword, page).getOrElse { emptyList() }
                }
            }

            val allResults = deferredResults.awaitAll().flatten()
            results.addAll(allResults)

            if (results.isEmpty()) {
                Result.failure(Exception("未找到相关资源"))
            } else {
                Result.success(results.sortedByDescending { it.seeders })
            }
        }
    }

    suspend fun searchByRule(ruleId: String, keyword: String, page: Int = 1): Result<List<MagnetItem>> {
        val rule = rules.find { it.id == ruleId } ?: return Result.failure(Exception("规则不存在"))
        return ruleParser.search(rule, keyword, page)
    }

    fun getFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()

    suspend fun addFavorite(item: MagnetItem) {
        val favorite = Favorite(
            title = item.title,
            magnetLink = item.magnetLink,
            fileSize = item.fileSize,
            sourceName = item.sourceName
        )
        favoriteDao.insert(favorite)
    }

    suspend fun removeFavorite(magnetLink: String) {
        favoriteDao.deleteByMagnetLink(magnetLink)
    }

    suspend fun isFavorite(magnetLink: String): Boolean {
        return favoriteDao.isFavorite(magnetLink) > 0
    }

    suspend fun clearFavorites() {
        favoriteDao.deleteAll()
    }

    fun getSearchHistory(): Flow<List<SearchHistory>> = searchHistoryDao.getAllHistory()

    fun getSearchKeywords(): Flow<List<String>> = searchHistoryDao.getKeywords()

    suspend fun addSearchHistory(keyword: String) {
        if (keyword.isBlank()) return
        searchHistoryDao.insert(SearchHistory(keyword = keyword))
    }

    suspend fun removeSearchHistory(keyword: String) {
        searchHistoryDao.deleteByKeyword(keyword)
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.deleteAll()
    }
}
