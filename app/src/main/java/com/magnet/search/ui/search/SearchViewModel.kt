package com.magnet.search.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnet.search.data.model.MagnetItem
import com.magnet.search.data.model.SearchRule
import com.magnet.search.domain.MagnetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: MagnetRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MagnetItem>>(emptyList())
    val searchResults: StateFlow<List<MagnetItem>> = _searchResults.asStateFlow()

    private val _currentKeyword = MutableStateFlow("")
    val currentKeyword: StateFlow<String> = _currentKeyword.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _favoriteStatus = MutableLiveData<Map<String, Boolean>>()
    val favoriteStatus: LiveData<Map<String, Boolean>> = _favoriteStatus

    val rules: List<SearchRule> get() = repository.getRules()

    fun search(keyword: String) {
        if (keyword.isBlank()) return
        
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            _currentKeyword.value = keyword
            _currentPage.value = 1

            repository.addSearchHistory(keyword)
            
            val result = repository.search(keyword, 1)
            
            result.fold(
                onSuccess = { items ->
                    _searchResults.value = items
                    _hasMore.value = items.size >= 20
                    _searchState.value = SearchState.Success(items.size)
                    updateFavoriteStatus(items)
                },
                onFailure = { error ->
                    _searchState.value = SearchState.Error(error.message ?: "搜索失败")
                }
            )
        }
    }

    fun loadMore() {
        if (!_hasMore.value || _currentKeyword.value.isBlank()) return
        
        viewModelScope.launch {
            _searchState.value = SearchState.LoadingMore
            val nextPage = _currentPage.value + 1
            
            val result = repository.search(_currentKeyword.value, nextPage)
            
            result.fold(
                onSuccess = { newItems ->
                    val currentList = _searchResults.value.toMutableList()
                    currentList.addAll(newItems)
                    _searchResults.value = currentList
                    _currentPage.value = nextPage
                    _hasMore.value = newItems.size >= 20
                    _searchState.value = SearchState.Success(newItems.size)
                    updateFavoriteStatus(newItems)
                },
                onFailure = { error ->
                    _searchState.value = SearchState.Error(error.message ?: "加载失败")
                }
            )
        }
    }

    fun toggleFavorite(item: MagnetItem) {
        viewModelScope.launch {
            val currentStatus = _favoriteStatus.value?.get(item.magnetLink) ?: false
            if (currentStatus) {
                repository.removeFavorite(item.magnetLink)
            } else {
                repository.addFavorite(item)
            }
            checkFavoriteStatus(item.magnetLink)
        }
    }

    fun checkFavoriteStatus(magnetLink: String) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(magnetLink)
            val statusMap = _favoriteStatus.value?.toMutableMap() ?: mutableMapOf()
            statusMap[magnetLink] = isFav
            _favoriteStatus.value = statusMap
        }
    }

    private fun updateFavoriteStatus(items: List<MagnetItem>) {
        viewModelScope.launch {
            val statusMap = _favoriteStatus.value?.toMutableMap() ?: mutableMapOf()
            items.forEach { item ->
                statusMap[item.magnetLink] = repository.isFavorite(item.magnetLink)
            }
            _favoriteStatus.value = statusMap
        }
    }
}

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    object LoadingMore : SearchState()
    data class Success(val count: Int) : SearchState()
    data class Error(val message: String) : SearchState()
}
