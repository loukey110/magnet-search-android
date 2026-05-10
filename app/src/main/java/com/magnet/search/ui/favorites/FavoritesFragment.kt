package com.magnet.search.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.magnet.search.App
import com.magnet.search.data.model.Favorite
import com.magnet.search.databinding.FragmentFavoritesBinding
import com.magnet.search.ui.adapter.FavoriteAdapter
import com.magnet.search.utils.ClipboardUtils
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(App.repository)
    }

    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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
        adapter = FavoriteAdapter(
            onCopyClick = { item -> copyMagnetLink(item) },
            onDeleteClick = { item -> viewModel.removeFavorite(item) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { favorites ->
                adapter.submitList(favorites)
                binding.tvEmpty.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun copyMagnetLink(item: Favorite) {
        ClipboardUtils.copyMagnetLink(requireContext(), item.magnetLink)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
