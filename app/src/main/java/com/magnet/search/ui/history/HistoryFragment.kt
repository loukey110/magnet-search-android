package com.magnet.search.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

    private var viewModel: HistoryViewModel? = null

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
        
        val repo = App.repository
        if (repo == null) {
            Toast.makeText(requireContext(), "初始化失败", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel = HistoryViewModel(repo)
        
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = HistoryAdapter(
            onItemClick = { keyword -> searchWithKeyword(keyword) },
            onDeleteClick = { keyword -> viewModel?.removeHistory(keyword) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel?.history?.collect { history ->
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
