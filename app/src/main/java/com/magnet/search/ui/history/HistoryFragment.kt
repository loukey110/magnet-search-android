package com.magnet.search.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.magnet.search.App
import com.magnet.search.databinding.FragmentHistoryBinding
import com.magnet.search.ui.adapter.HistoryAdapter
import com.magnet.search.ui.search.SearchFragment
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(App.repository)
    }

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClearButton()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onItemClick = { keyword -> searchWithKeyword(keyword) },
            onDeleteClick = { keyword -> viewModel.removeHistory(keyword) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.history.collect { history ->
                adapter.submitList(history)
                binding.tvEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupClearButton() {
        binding.btnClear.setOnClickListener {
            showClearDialog()
        }
    }

    private fun searchWithKeyword(keyword: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.magnet.search.R.id.fragmentContainer, SearchFragment().apply {
                arguments = Bundle().apply {
                    putString("keyword", keyword)
                }
            })
            .commit()
        
        requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
            com.magnet.search.R.id.bottomNavigation
        ).selectedItemId = com.magnet.search.R.id.nav_search
    }

    private fun showClearDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("清空历史")
            .setMessage("确定要清空所有搜索历史吗？")
            .setPositiveButton("清空") { _, _ ->
                viewModel.clearAll()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
