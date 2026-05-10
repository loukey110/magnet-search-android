package com.magnet.search.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnet.search.data.model.SearchHistory
import com.magnet.search.domain.MagnetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: MagnetRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<SearchHistory>>(emptyList())
    val history: StateFlow<List<SearchHistory>> = _history.asStateFlow()

    private val _keywords = MutableStateFlow<List<String>>(emptyList())
    val keywords: StateFlow<List<String>> = _keywords.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getSearchHistory().collect { list ->
                _history.value = list
            }
        }
        viewModelScope.launch {
            repository.getSearchKeywords().collect { list ->
                _keywords.value = list
            }
        }
    }

    fun removeHistory(keyword: String) {
        viewModelScope.launch {
            repository.removeSearchHistory(keyword)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }
}
