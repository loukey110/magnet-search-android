package com.magnet.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magnet.search.databinding.ItemHistoryBinding
import com.magnet.search.data.model.SearchHistory

class HistoryAdapter(
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<SearchHistory, HistoryAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchHistory) {
            binding.tvKeyword.text = item.keyword
            binding.root.setOnClickListener { onItemClick(item.keyword) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item.keyword) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SearchHistory>() {
        override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
            return oldItem == newItem
        }
    }
}
