package com.magnet.search.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.magnet.search.App
import com.magnet.search.R
import com.magnet.search.databinding.FragmentHistoryBinding
import com.magnet.search.ui.adapter.HistoryAdapter
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
        
        try {
            setupRecyclerView()
            setupObservers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onItemClick = { keyword -> searchWithKeyword(keyword) },
            onDeleteClick = { keyword -> viewModel.removeHistory(keyword) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.history.collect { history ->
                adapter.submitList(history)
                binding.tvEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun searchWithKeyword(keyword: String) {
        activity?.let { act ->
            act.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNavigation
            )?.selectedItemId = R.id.nav_search
            
            act.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.magnet.search.ui.search.SearchFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
