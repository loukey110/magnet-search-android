package com.magnet.search.ui.favorites

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
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(
            onCopyClick = { item -> copyMagnetLink(item) },
            onDeleteClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
        }
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

    private fun showDeleteDialog(item: Favorite) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("删除收藏")
            .setMessage("确定要删除此收藏吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.removeFavorite(item)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
