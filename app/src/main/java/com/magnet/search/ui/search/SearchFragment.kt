package com.magnet.search.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.magnet.search.App
import com.magnet.search.R
import com.magnet.search.data.model.MagnetItem
import com.magnet.search.data.model.SearchRule
import com.magnet.search.databinding.FragmentSearchBinding
import com.magnet.search.ui.adapter.MagnetAdapter
import com.magnet.search.utils.ClipboardUtils
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(App.repository)
    }

    private lateinit var adapter: MagnetAdapter
    private var selectedRuleId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
        setupObservers()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        adapter = MagnetAdapter(
            onCopyClick = { item -> copyMagnetLink(item) },
            onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.btnFilter.setOnClickListener {
            showSourceSelector()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            val keyword = viewModel.currentKeyword.value
            if (keyword.isNotEmpty()) {
                viewModel.search(keyword)
            } else {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collect { state ->
                when (state) {
                    is SearchState.Idle -> showIdle()
                    is SearchState.Loading -> showLoading()
                    is SearchState.LoadingMore -> showLoadingMore()
                    is SearchState.Success -> showSuccess(state.count)
                    is SearchState.Error -> showError(state.message)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                adapter.submitList(results)
                binding.tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewModel.favoriteStatus.observe(viewLifecycleOwner) { statusMap ->
            adapter.setFavoriteStatusMap(statusMap)
        }
    }

    private fun performSearch() {
        val keyword = binding.etSearch.text.toString().trim()
        if (keyword.isNotEmpty()) {
            viewModel.search(keyword)
        }
    }

    private fun showSourceSelector() {
        val rules = viewModel.rules
        if (rules.isEmpty()) return

        val names = arrayOf("全部来源") + rules.map { it.name }.toTypedArray()
        val selected = if (selectedRuleId == null) 0 else rules.indexOfFirst { it.id == selectedRuleId } + 1

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择搜索源")
            .setSingleChoiceItems(names, selected) { dialog, which ->
                selectedRuleId = if (which == 0) null else rules[which - 1].id
                dialog.dismiss()
            }
            .show()
    }

    private fun copyMagnetLink(item: MagnetItem) {
        ClipboardUtils.copyMagnetLink(requireContext(), item.magnetLink)
    }

    private fun showIdle() {
        binding.swipeRefresh.isRefreshing = false
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
    }

    private fun showLoading() {
        binding.swipeRefresh.isRefreshing = true
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }

    private fun showLoadingMore() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showSuccess(count: Int) {
        binding.swipeRefresh.isRefreshing = false
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.swipeRefresh.isRefreshing = false
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
