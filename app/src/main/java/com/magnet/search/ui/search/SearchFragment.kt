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
import com.magnet.search.App
import com.magnet.search.R
import com.magnet.search.data.model.MagnetItem
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
        
        try {
            setupRecyclerView()
            setupSearchBar()
            setupObservers()
            setupSwipeRefresh()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        adapter = MagnetAdapter(
            onCopyClick = { item -> copyMagnetLink(item) },
            onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
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
                    is SearchState.Idle -> {
                        binding.swipeRefresh.isRefreshing = false
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                    }
                    is SearchState.Loading -> {
                        binding.swipeRefresh.isRefreshing = true
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvError.visibility = View.GONE
                    }
                    is SearchState.LoadingMore -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is SearchState.Success -> {
                        binding.swipeRefresh.isRefreshing = false
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                    }
                    is SearchState.Error -> {
                        binding.swipeRefresh.isRefreshing = false
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = state.message
                    }
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
            if (statusMap != null) {
                adapter.setFavoriteStatusMap(statusMap)
            }
        }
    }

    private fun performSearch() {
        val keyword = binding.etSearch.text.toString().trim()
        if (keyword.isNotEmpty()) {
            viewModel.search(keyword)
        }
    }

    private fun copyMagnetLink(item: MagnetItem) {
        ClipboardUtils.copyMagnetLink(requireContext(), item.magnetLink)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
