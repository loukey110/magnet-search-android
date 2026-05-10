package com.magnet.search

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.magnet.search.data.MagnetItem
import com.magnet.search.data.SearchEngine
import com.magnet.search.ui.MagnetAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvError: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    
    private lateinit var adapter: MagnetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        setupListeners()
    }
    
    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvError = findViewById(R.id.tvError)
        swipeRefresh = findViewById(R.id.swipeRefresh)
    }
    
    private fun setupRecyclerView() {
        adapter = MagnetAdapter { item -> copyToClipboard(item) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupListeners() {
        btnSearch.setOnClickListener { performSearch() }
        
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
        
        swipeRefresh.setOnRefreshListener {
            val keyword = etSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                doSearch(keyword)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
    }
    
    private fun performSearch() {
        val keyword = etSearch.text.toString().trim()
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }
        doSearch(keyword)
    }
    
    private fun doSearch(keyword: String) {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val results = SearchEngine.search(keyword)
                showResults(results)
            } catch (e: Exception) {
                showError(e.message ?: "搜索失败")
            }
        }
    }
    
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        tvError.visibility = View.GONE
        swipeRefresh.isRefreshing = true
    }
    
    private fun showResults(results: List<MagnetItem>) {
        progressBar.visibility = View.GONE
        swipeRefresh.isRefreshing = false
        
        if (results.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            tvError.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            tvError.visibility = View.GONE
            adapter.setData(results)
        }
        
        Toast.makeText(this, "找到 ${results.size} 个结果", Toast.LENGTH_SHORT).show()
    }
    
    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        swipeRefresh.isRefreshing = false
        tvEmpty.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }
    
    private fun copyToClipboard(item: MagnetItem) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("磁力链接", item.magnetLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已复制磁力链接", Toast.LENGTH_SHORT).show()
    }
}
